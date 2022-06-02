package com.pwhintek.backend.controller;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.json.JSONUtil;
import com.pwhintek.backend.dto.Result;
import com.pwhintek.backend.dto.SignDTO;
import com.pwhintek.backend.entity.User;
import com.pwhintek.backend.exception.userinfo.UserInfoUpdateFailException;
import com.pwhintek.backend.exception.userinfo.UserInfoIdempotenceException;
import com.pwhintek.backend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Set;

import static com.pwhintek.backend.constant.UserInfoConstants.U_ALLOW_UPDATE;

/**
 * 接收前端登录模块请求，控制用户登入登出
 *
 * @author ChillyBlaze
 * @version 1.0
 * @since 21/04/2022
 */
@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    /**
     * 测试api接口
     *
     * @author ChillyBlaze
     * @since 25/04/2022 19:33
     */
    @PostMapping("/test")
    public Result test() {
        System.out.println("fail");
        return Result.ok("1231");
    }

    /**
     * 注册控制
     *
     * @author ChillyBlaze
     * @since 22/04/2022 15:58
     */
    @PostMapping("/signup")
    public Result signup(@RequestBody SignDTO signDTO) {
        userService.signUp(signDTO);
        return Result.ok();
    }

    /**
     * 登录控制，初始权限赋予
     *
     * @author ChillyBlaze
     * @since 22/04/2022 20:03
     */
    @PostMapping("/login")
    public Result login(@RequestBody SignDTO signDTO) {
        if (StpUtil.isLogin()) throw UserInfoIdempotenceException.getLoginInstance(signDTO);
        userService.login(signDTO);
        return Result.ok();
    }

    /**
     * 查询指定用户控制
     *
     * @author ChillyBlaze
     * @since 24/04/2022 14:16
     */
    @GetMapping("/info/{name}")
    public Result Info(@PathVariable("name") String username) {
        return Result.ok(userService.userInfo(username, r -> userService.lambdaQuery().eq(User::getUsername, r).one()));
    }

    /**
     * 用户信息控制
     *
     * @author ChillyBlaze
     * @since 23/04/2022 16:40
     */
    @GetMapping("/my_info")
    public Result myInfo() {
        String id = StpUtil.getLoginIdAsString();
        return Result.ok(userService.userInfo(id, userService::getById));
    }

    /**
     * 用户登出控制
     *
     * @author ChillyBlaze
     * @since 24/04/2022 13:44
     */
    @PostMapping("/logout")
    public Result logout() {
        StpUtil.logout();
        return Result.ok();
    }

    /**
     * 用户信息删除控制
     *
     * @author ChillyBlaze
     * @since 24/04/2022 13:59
     */
    @PostMapping("/delete")
    public Result deleteMe() {
        userService.deleteUser(StpUtil.getLoginIdAsString());
        return Result.ok();
    }

    /**
     * 修改各种信息
     *
     * @author ChillyBlaze
     * @since 24/04/2022 21:10
     */
    @PostMapping("/update")
    public Result updateInfo(@RequestBody Map<String, String> map) {
        Set<String> set = map.keySet();
        for (String s : set) {
            if (!U_ALLOW_UPDATE.contains(s)) {
                String id = StpUtil.getLoginIdAsString();
                String data = JSONUtil.createObj().set("id", id).set(s, map.get(s)).toString();
                throw UserInfoUpdateFailException.getInstance(data);
            }
            userService.updateInfo(map.get(s), s);
        }
        return Result.ok();
    }

}
