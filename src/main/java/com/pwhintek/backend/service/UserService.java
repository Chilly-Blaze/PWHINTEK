package com.pwhintek.backend.service;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.pwhintek.backend.dto.DetailedUserInfoDTO;
import com.pwhintek.backend.dto.Result;
import com.pwhintek.backend.dto.SignDTO;
import com.pwhintek.backend.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.pwhintek.backend.utils.RedisStorageSolution;

import java.util.function.Function;

/**
 * UserService接口
 *
 * @author chillyblaze
 * @since 2022-04-22 11:07:13
 */
public interface UserService extends IService<User> {

    void signUp(SignDTO signDTO);

    void login(SignDTO signDTO);

    DetailedUserInfoDTO userInfo(String column, Function<String, User> f);

    void updateInfo(String updateInfo, String type);

    void deleteUser(String id);
}
