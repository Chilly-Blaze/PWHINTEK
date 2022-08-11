package com.pwhintek.backend.exception.userinfo;

import cn.hutool.json.JSONUtil;
import com.pwhintek.backend.dto.DetailedUserInfoDTO;
import com.pwhintek.backend.dto.SignDTO;
import lombok.Getter;

/**
 * 用户提交信息异常处理，用户操作相关父级异常
 *
 * @author ChillyBlaze
 * @version 1.0
 * @since 2022 Apr 22 16:53
 */
@Getter
public class UserInfoException extends RuntimeException {
    private final String errorData;

    public UserInfoException(String message, SignDTO data) {
        super(message);
        this.errorData = JSONUtil.toJsonStr(data);
    }

    public UserInfoException(String message, String data) {
        super(message);
        this.errorData = data;
    }
}
