package com.pwhintek.backend.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pwhintek.backend.dto.DetailedUserInfoDTO;
import com.pwhintek.backend.dto.SignDTO;
import com.pwhintek.backend.entity.User;
import com.pwhintek.backend.exception.userinfo.ErrorLoginException;
import com.pwhintek.backend.exception.userinfo.NotFoundInfoException;
import com.pwhintek.backend.exception.userinfo.UserInfoIdempotenceException;
import com.pwhintek.backend.exception.userinfo.UserInfoUpdateFailException;
import com.pwhintek.backend.mapper.UserMapper;
import com.pwhintek.backend.service.UserService;
import com.pwhintek.backend.utils.RedisStorageSolution;
import com.pwhintek.backend.utils.RegexUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static com.pwhintek.backend.constant.RedisConstants.*;
import static com.pwhintek.backend.constant.UserInfoConstants.*;

/**
 * 针对表【t_user(用户信息表)】的数据库操作Service实现
 *
 * @author chillyblaze
 * @since 2022-04-22 11:07:13
 */
@Service
@AllArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private RedisStorageSolution redisStorageSolution;

    /**
     * 注册服务实现
     *
     * @param signUpForm 前端传递表单信息
     * @author ChillyBlaze
     */
    @Override
    // TODO: 需要思考等幂性问题
    public void signUp(SignDTO signUpForm) {
        // 获取表单信息
        String username = signUpForm.getUsername();
        String password = signUpForm.getPassword();
        String nickname = signUpForm.getNickname();

        // 验证信息是否正确合法，如果不合法就抛出异常
        RegexUtils.isUsername(username);
        RegexUtils.isPassword(password);
        RegexUtils.isNickName(nickname);

        // 验证数据库中是否存在该用户
        if (lambdaQuery().eq(User::getUsername, username).exists())
            throw UserInfoIdempotenceException.getSignUpInstance(signUpForm);

        // 没有，则存入数据库，返回响应
        signUpForm.setPassword(DigestUtil.sha256Hex(password));
        save(BeanUtil.toBean(signUpForm, User.class));
    }

    /**
     * 登录服务实现
     *
     * @author ChillyBlaze
     * @since 23/04/2022 21:56
     */
    @Override
    public void login(SignDTO signDTO) {
        // 判断用户名密码是否合法
        String username = signDTO.getUsername();
        String password = signDTO.getPassword();
        RegexUtils.isUsername(username);
        RegexUtils.isPassword(password);
        // 验证用户名密码正确性
        password = DigestUtil.sha256Hex(password);
        User user = lambdaQuery()
                .eq(User::getUsername, username)
                .eq(User::getPassword, password)
                .one();
        // 错误，抛出异常，返回错误
        if (ObjectUtil.isNull(user))
            throw ErrorLoginException.getInstance(signDTO);
        // 获取用户id登录
        Long id = user.getId();
        StpUtil.login(id);
        // 将用户信息存入Redis中
        DetailedUserInfoDTO dto = BeanUtil.copyProperties(user, DetailedUserInfoDTO.class);
        redisStorageSolution.saveByTTL(USER_PREFIX + INFO_PREFIX + id, dto, USER_INFO_TTL, TimeUnit.MINUTES);
    }

    /**
     * 查询Redis或数据库中的信息
     *
     * @author ChillyBlaze
     * @since 23/04/2022 21:58
     */
    @Override
    public DetailedUserInfoDTO userInfo(String column, Function<String, User> method) {
        User user = redisStorageSolution.queryWithPassThrough(USER_PREFIX + INFO_PREFIX, column, User.class, method, USER_INFO_TTL, TimeUnit.MINUTES);
        if (ObjectUtil.isNull(user)) {
            throw NotFoundInfoException.getUserInstance();
        }
        return BeanUtil.copyProperties(user, DetailedUserInfoDTO.class);
    }

    /**
     * 用户登出，逻辑删除用户信息
     *
     * @author ChillyBlaze
     * @since 24/04/2022 14:13
     */
    @Override
    public void deleteUser(String id) {
        StpUtil.logout(id);
        removeById(id);
        redisStorageSolution.deleteByKey(USER_PREFIX + INFO_PREFIX + id);
    }

    /**
     * 更新个人头像信息
     *
     * @param file 用户上传文件
     * @return 生成新头像名称
     */
    @Override
    public String updateAvatar(MultipartFile file) throws IOException {
        // 从数据库获取个人头像路径
        String portrait = getById(StpUtil.getLoginIdAsString()).getPortrait();
        // 删除原有头像
        if (!portrait.equals("default.png")) {
            FileUtil.del(U_AVATAR_DIR + portrait);
        }

        // 生成随机名称
        String filename = IdUtil.fastSimpleUUID() + "." + FileUtil.getSuffix(file.getOriginalFilename());
        // 存储图片
        FileUtil.writeFromStream(file.getInputStream(), U_AVATAR_DIR + filename);
        // 更新头像名称信息
        updateInfo(filename, DATABASE_U_PORTRAIT);
        // 返回文件名称
        return filename;
    }

    /**
     * 更新个人指定信息
     *
     * @param updateInfo 修改信息内容
     * @param type       DATABASE_U开头常量
     */
    public void updateInfo(String updateInfo,
                           String type) {
        // 获取锁
        String id = StpUtil.getLoginIdAsString();
        String key = USER_PREFIX + LOCK_PREFIX + id;
        String keyUser = USER_PREFIX + INFO_PREFIX + id;
        // 保证更新操作幂等
        try {
            if (!redisStorageSolution.tryLock(key)) {
                throw UserInfoIdempotenceException.getUpdateInstance(updateInfo, type);
            }
            // 数据库更新
            // 密码需要加密
            if (type.equals(DATABASE_U_PASSWORD)) {
                updateInfo = DigestUtil.sha256Hex(updateInfo);
            }
            String s = JSONUtil.createObj().set("id", id).set(type, updateInfo).toString();
            // 用户名不允许重复
            if (type.equals(DATABASE_U_USERNAME) && lambdaQuery().eq(User::getUsername, updateInfo).exists()) {
                throw UserInfoUpdateFailException.getInstance(INVALID_USERNAME, s);
            }
            UpdateWrapper<User> wrapper = new UpdateWrapper<>();
            wrapper.eq(DATABASE_U_ID, id).set(type, updateInfo);
            if (!update(wrapper)) {
                throw UserInfoUpdateFailException.getInstance(s);
            }
            // 删除redis信息
            redisStorageSolution.deleteByKey(keyUser);
        } finally {
            // 释放锁
            redisStorageSolution.unlock(key);
        }
    }
}




