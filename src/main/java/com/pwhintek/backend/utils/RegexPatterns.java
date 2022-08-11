package com.pwhintek.backend.utils;

/**
 * 一些正则表达式
 *
 * @author ChillyBlaze
 * @version 1.0
 * @since 22/04/2022
 */
public class RegexPatterns {
    public static final String USERNAME_REGEX = "^\\w{4,32}$";
    public static final String PASSWORD_REGEX = "^[A-Za-z0-9._~!@#$^&*]{6,}$";
    public static final String NICKNAME_REGEX = "^[\\u4e00-\\u9fa5\\w\\u0800-\\u4e00\\x20]{1,10}$";
}
