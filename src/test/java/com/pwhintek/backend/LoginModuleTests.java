package com.pwhintek.backend;

import cn.dev33.satoken.fun.SaParamRetFunction;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.func.Func;
import cn.hutool.core.lang.func.Func0;
import cn.hutool.core.lang.func.Func1;
import cn.hutool.core.lang.func.LambdaUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.LambdaUtils;
import com.baomidou.mybatisplus.core.toolkit.support.LambdaMeta;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.pwhintek.backend.dto.DetailedUserInfoDTO;
import com.pwhintek.backend.dto.SignDTO;
import com.pwhintek.backend.entity.User;
import com.pwhintek.backend.exception.userinfo.UserInfoException;
import com.pwhintek.backend.service.UserService;
import com.pwhintek.backend.utils.RegexUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

@SpringBootTest
class LoginModuleTests {

    @Autowired
    private UserService userService;

    @Test
    void testDatabaseInteraction() {
        UpdateWrapper<User> wrapper = new UpdateWrapper<>();
        wrapper.like("username", "12345").set("permission", 1);
        userService.update(null, wrapper);
    }

    @Test
    void testRegex() {
        RegexUtils.isNickName("123");
    }

    @Test
    void testBeanUtil() {
        SignDTO dto = new SignDTO();
        dto.setUsername("123");
        dto.setPassword("123");
        dto.setNickname("123");
        User user = BeanUtil.toBean(dto, User.class);
        System.out.println("user = " + user);
    }

    @Test
    void testSignUpService() {
        SignDTO dto = new SignDTO();
        dto.setUsername("123124");
        dto.setPassword("123123");
        dto.setNickname("123");
    }

    @Test
    void testDigester() {
        System.out.println(DigestUtil.sha256Hex("111"));
        System.out.println("f6e0a1e2ac41945a9aa7ff8a8aaa0cebc12a3bcc981a929ad5cf810a090e11ae".length());
    }

    @Test
    void testException() {
        SignDTO dto = new SignDTO();
        dto.setUsername("123124");
        dto.setPassword("123123");
        try {
            userService.login(dto);
            System.out.println();
        } catch (UserInfoException e) {
            System.out.println(StrUtil.format("{},{},{}", e.getClass(), e.getMessage(), e.getErrorData()));
        }
    }

    @Test
    void testQueryPermission() {
        String permission = userService.lambdaQuery()
                .eq(User::getUsername, "123123")
                .select(User::getPermission)
                .one()
                .getPermission();
    }

    @Test
    void testQueryUserDTO() {
        // 传入DTO，则需要校验数据库是否可以查DTO正确赋值
        // 传入user，则需要校验就JSON后的DTO是否可以toBean到User✅
        User user = userService.lambdaQuery()
                .eq(User::getUsername, "123123")
                .one();
        DetailedUserInfoDTO dto = new DetailedUserInfoDTO();
        dto.setUsername("1233");
        String s = JSONUtil.toJsonStr(dto);
        User bean = JSONUtil.toBean(s, User.class);
        System.out.println(bean);
    }

    @Test
    void testQueryByName() {
        System.out.println(userService.userInfo("123123", r -> userService.lambdaQuery().eq(User::getUsername, r).one()));
    }

    @Test
    void testUpdate() {
        String password = DigestUtil.sha256Hex("123125");
        // LambdaUpdateWrapper<User> wrapper = new LambdaUpdateWrapper<>();
        // wrapper.set(User::getPassword,password).eq(User::getUsername,"123123");
        UpdateWrapper<User> wrapper = new UpdateWrapper<>();
        wrapper.set("password", password).eq("user_id", 1517840565344063489L);
        System.out.println(userService.update(wrapper));
    }

    void testUserGetToString(SFunction<User, String> f) {
        String s = LambdaUtils.extract(f).getImplMethodName();
        System.out.println(s);
    }

    @Test
    void testFunctionPara() {
        testUserGetToString(User::getPassword);
    }

    @Test
    void testJSONUtil() {
        System.out.println(JSONUtil.createObj().set("id", "123").toString());
    }

    @Test
    void testStaticList() {
        final List<String> l = Arrays.asList("123", "1", "2");
        System.out.println(l.contains("123"));
    }

    @Test
    void testUploader() {
        System.out.println(IdUtil.fastSimpleUUID());
        System.out.println(IdUtil.simpleUUID());
    }

}
