package com.pwhintek.backend.constant;

import java.util.Arrays;
import java.util.List;

/**
 * 用户异常信息常量
 *
 * @author ChillyBlaze
 * @version 1.0
 * @since 2022 Apr 23 20:37
 */
public class UserInfoConstants {
    public static final String DATABASE_U_ID = "user_id";
    public static final String DATABASE_U_USERNAME = "username";
    public static final String DATABASE_U_PASSWORD = "password";
    public static final String DATABASE_U_AVATAR = "avatar";
    public static final String DATABASE_U_NICKNAME = "nickname";
    public static final String DATABASE_U_PERMISSION = "permission";
    public static final String DATABASE_U_IS_DELETE = "is_delete";
    public static final String DATABASE_U_CREATE_TIME = "create_time";

    public static final String INVALID_USERNAME = "用户名";
    public static final String INVALID_PASSWORD = "密码";
    public static final String INVALID_NICKNAME = "昵称";

    public static final String HINT_INFO = "你居然绕过了前端检测，真不错，但是だめ，你的{}还是很有问题!";

    public static final String VERIFY_ERROR = "用户名或密码输错啦";
    public static final String NO_LOGIN_ERROR = "你还没有登录呢！";

    public static final String NOT_FOUND_USER_ERROR = "遇到了奇怪问题，居然没找到这个用户";
    public static final String NOT_FOUND_AVATAR_ERROR = "遇到了奇怪问题，居然没找到这个头像";

    public static final String DUPLICATION_SIGN_UP = "你曾经已经注册过了哦，我猜你可能忘记了密码？";
    public static final String DUPLICATION_LOGIN = "再怎么点也不会让你登录两次的啦！";

    public static final String UPDATE_DUPLICATION = "你的{}信息已经被人占用了！还是考虑一个新{}吧！";

    public static final List<String> U_ALLOW_UPDATE = Arrays.asList(DATABASE_U_USERNAME, DATABASE_U_PASSWORD, DATABASE_U_NICKNAME);
    public static final String U_AVATAR_DIR = "avatar/";
    public static final String U_DEFAULT_AVATAR_NAME = "default.png";
}
