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
    public static final String DATABASE_ID = "user_id";
    public static final String DATABASE_USERNAME = "username";
    public static final String DATABASE_PASSWORD = "password";
    public static final String DATABASE_PORTRAIT = "portrait";
    public static final String DATABASE_NICKNAME = "nickname";
    public static final String DATABASE_SIGNATURE = "signature";
    public static final String DATABASE_PERMISSION = "permission";
    public static final String DATABASE_IS_DELETE = "is_delete";
    public static final String DATABASE_CREATE_TIME = "create_time";

    public static final String INVALID_USERNAME = "用户名";
    public static final String INVALID_PASSWORD = "密码";
    public static final String INVALID_NICKNAME = "昵称";

    public static final String HINT_INFO = "你居然绕过了前端检测，真不错，但是だめ，你的{}还是很有问题!";

    public static final String VERIFY_ERROR = "用户名或密码输错啦";
    public static final String NO_LOGIN_ERROR = "你还没有登录呢！";

    public static final String NOT_FOUND_ERROR = "遇到了奇怪问题，居然没找到这个用户";

    public static final String DUPLICATION_SIGN_UP = "你曾经已经注册过了哦，我猜你可能忘记了密码？";
    public static final String DUPLICATION_LOGIN = "再怎么点也不会让你登录两次的啦！";
    public static final String DUPLICATION_UPDATE = "别骂了别骂了，再骂信息要更新不上去啦！";

    public static final String UPDATE_DUPLICATION = "你的{}信息已经被人占用了！还是考虑一个新{}吧！";
    public static final String UPDATE_ERROR = "你是不是想更新什么不得了的东西！";

    public static final List<String> ALLOW_UPDATE = Arrays.asList("username", "password", "nickname", "signature", "portrait");
}
