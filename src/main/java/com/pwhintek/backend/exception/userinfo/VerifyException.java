package com.pwhintek.backend.exception.userinfo;

import cn.hutool.core.util.StrUtil;
import com.pwhintek.backend.dto.SignDTO;

import static com.pwhintek.backend.constant.UserInfoConstants.*;

/**
 * 注册信息验证不合法
 *
 * @author ChillyBlaze
 * @version 1.0
 * @since 2022 Apr 23 14:59
 */
public class VerifyException extends UserInfoException {

    public VerifyException(String message, SignDTO data) {
        super(message, data);
    }

    public static UserInfoException getInstance(String type, String data) {
        String msg = StrUtil.format(HINT_INFO, type);
        SignDTO dto = new SignDTO();
        if (INVALID_USERNAME.equals(type))
            dto.setUsername(data);
        else if (INVALID_PASSWORD.equals(type))
            dto.setPassword(data);
        else if (INVALID_NICKNAME.equals(type))
            dto.setNickname(data);
        return new VerifyException(msg, dto);
    }
}
