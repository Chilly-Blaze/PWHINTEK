package com.pwhintek.backend.exception.userinfo;

import com.pwhintek.backend.dto.SignDTO;

import static com.pwhintek.backend.constant.UserInfoConstants.VERIFY_ERROR;

/**
 * 用户名密码校验失败
 *
 * @author ChillyBlaze
 * @version 1.0
 * @since 2022 Apr 23 15:15
 */
public class ErrorLoginException extends UserInfoException {


    public ErrorLoginException(String message, SignDTO data) {
        super(message, data);
    }

    public static UserInfoException getInstance(SignDTO data) {
        return new ErrorLoginException(VERIFY_ERROR, data);
    }
}
