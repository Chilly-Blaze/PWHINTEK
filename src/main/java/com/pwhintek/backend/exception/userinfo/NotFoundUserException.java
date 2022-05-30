package com.pwhintek.backend.exception.userinfo;

import com.pwhintek.backend.dto.SignDTO;

import static com.pwhintek.backend.constant.UserInfoConstants.NOT_FOUND_ERROR;

/**
 * @author ChillyBlaze
 * @version 1.0
 * @since 2022 Apr 23 22:48
 */
public class NotFoundUserException extends UserInfoException {
    public NotFoundUserException(String message, SignDTO data) {
        super(message, data);
    }

    public static NotFoundUserException getInstance() {
        return new NotFoundUserException(NOT_FOUND_ERROR, null);
    }
}
