package com.pwhintek.backend.service;

import com.pwhintek.backend.dto.DetailedUserInfoDTO;
import com.pwhintek.backend.dto.SignDTO;
import com.pwhintek.backend.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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

    String updateAvatar(MultipartFile file) throws IOException;
}
