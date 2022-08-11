package com.pwhintek.backend.utils;

import cn.hutool.core.util.StrUtil;
import com.pwhintek.backend.exception.userinfo.UserInfoException;
import com.pwhintek.backend.exception.userinfo.VerifyException;

import static com.pwhintek.backend.constant.UserInfoConstants.*;
import static com.pwhintek.backend.utils.RegexPatterns.*;

/**
 * 校验请求内容是否合法，静态链式调用，错误抛出异常verifyException
 *
 * @author ChillyBlaze
 * @version 1.0
 * @see UserInfoException
 * @since 22/04/2022
 */
public class RegexUtils {
    /**
     * 用户名是否为字符与数字，允许下划线，长度在4-32位之间
     *
     * @author ChillyBlaze
     */
    public static void isUsername(String username) {
        mismatch(username, USERNAME_REGEX, INVALID_USERNAME);
    }

    /**
     * 密码是否为字符与数字与下划线和._~!@#$^&*，长度在6-20位之间
     *
     * @author ChillyBlaze
     */
    public static void isPassword(String password) {
        mismatch(password, PASSWORD_REGEX, INVALID_PASSWORD);
    }

    /**
     * 用户名是否是中、日文字符和字母、数字、空格和下划线
     *
     * @author ChillyBlaze
     */
    public static void isNickName(String nickname) {
        mismatch(nickname, NICKNAME_REGEX, INVALID_NICKNAME);
    }

    /**
     * 校验是否为正则表达式，不是则抛出异常
     *
     * @author ChillyBlaze
     */
    private static void mismatch(String str, String regex, String error) {
        if (StrUtil.isBlank(str) || !str.matches(regex)) {
            throw VerifyException.getInstance(error, str);
        }
    }
}
