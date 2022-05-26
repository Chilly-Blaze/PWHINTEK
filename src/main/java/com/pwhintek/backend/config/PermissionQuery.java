package com.pwhintek.backend.config;

import cn.dev33.satoken.stp.StpInterface;
import cn.hutool.core.convert.Convert;
import com.pwhintek.backend.entity.User;
import com.pwhintek.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户组查询
 *
 * @author ChillyBlaze
 * @version 1.0
 * @since 2022 Apr 23 20:02
 */
@Configuration
public class PermissionQuery implements StpInterface {

    @Resource
    private UserService userService;

    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        return null;
    }

    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        Long id = Convert.toLong(loginId);
        String permission = userService.lambdaQuery()
                .eq(User::getId, id)
                .select(User::getPermission)
                .one()
                .getPermission();
        List<String> list = new ArrayList<>();
        list.add(permission);
        return list;
    }
}
