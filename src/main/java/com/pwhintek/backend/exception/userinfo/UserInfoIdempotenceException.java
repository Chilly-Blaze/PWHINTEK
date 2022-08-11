package com.pwhintek.backend.exception.userinfo;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.pwhintek.backend.dto.DetailedUserInfoDTO;
import com.pwhintek.backend.dto.SignDTO;
import com.pwhintek.backend.entity.User;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import static com.pwhintek.backend.constant.GlobalConstants.DUPLICATION_UPDATE;
import static com.pwhintek.backend.constant.UserInfoConstants.*;

/**
 * 用户重复操作异常，保证接口幂等
 *
 * @author ChillyBlaze
 * @version 1.0
 * @since 2022 Apr 23 15:04
 */
public class UserInfoIdempotenceException extends UserInfoException {
    public UserInfoIdempotenceException(String message, String data) {
        super(message, data);
    }

    public UserInfoIdempotenceException(String message, SignDTO data) {
        super(message, data);
    }

    /**
     * 重复注册
     *
     * @author ChillyBlaze
     * @since 22/04/2022 19:56
     */
    public static UserInfoIdempotenceException getSignUpInstance(SignDTO data) {
        return new UserInfoIdempotenceException(DUPLICATION_SIGN_UP, data);
    }

    /**
     * 重复登录
     *
     * @author ChillyBlaze
     * @since 22/04/2022 19:56
     */
    public static UserInfoIdempotenceException getLoginInstance(SignDTO data) {
        return new UserInfoIdempotenceException(DUPLICATION_LOGIN, data);
    }

    /**
     * 重复更新
     *
     * @author ChillyBlaze
     * @since 25/04/2022 09:31
     */
    public static UserInfoIdempotenceException getUpdateInstance(String data, String type) {
        JSONObject set = JSONUtil.createObj().set("id", StpUtil.getLoginIdAsString()).set(type, data);
        String s = JSONUtil.toJsonStr(set);
        return new UserInfoIdempotenceException(DUPLICATION_UPDATE, s);
    }
}
