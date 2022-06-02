package com.pwhintek.backend.exception.userinfo;

import com.pwhintek.backend.dto.SignDTO;

/**
 * @author ChillyBlaze
 * @version 1.0
 * @since 2022 Jun 02 22:34
 */
public class UserInfoDeleteFailException extends UserInfoException {
    public UserInfoDeleteFailException(String message, String data) {
        super(message, data);
    }
}
