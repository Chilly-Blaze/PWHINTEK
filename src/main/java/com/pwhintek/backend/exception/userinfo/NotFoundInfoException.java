package com.pwhintek.backend.exception.userinfo;

import com.pwhintek.backend.dto.SignDTO;

import static com.pwhintek.backend.constant.UserInfoConstants.NOT_FOUND_AVATAR_ERROR;
import static com.pwhintek.backend.constant.UserInfoConstants.NOT_FOUND_USER_ERROR;

/**
 * @author ChillyBlaze
 * @version 1.0
 * @since 2022 Apr 23 22:48
 */
public class NotFoundInfoException extends UserInfoException {
    public NotFoundInfoException(String message, SignDTO data) {
        super(message, data);
    }

    public static NotFoundInfoException getUserInstance() {
        return new NotFoundInfoException(NOT_FOUND_USER_ERROR, null);
    }

    public static NotFoundInfoException getAvatarInstance() {
        return new NotFoundInfoException(NOT_FOUND_AVATAR_ERROR, null);
    }
}
