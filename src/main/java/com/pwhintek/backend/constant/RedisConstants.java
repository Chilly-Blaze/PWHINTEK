package com.pwhintek.backend.constant;

/**
 * 有关存储到redis中的各种时间
 *
 * @author ChillyBlaze
 * @version 1.0
 * @since 2022 Apr 22 20:58
 */
public class RedisConstants {
    public static final Long CACHE_NULL_TTL = 2L;
    public static final String USER_PREFIX = "user:";
    public static final String ARTICLE_PREFIX = "article:";

    public static final Long USER_INFO_TTL = 30L;
    public static final Long ARTICLE_TTL = 2L;
    public static final String INFO_PREFIX = "info:";

    public static final Long LOCK_TTL = 10L;
    public static final String LOCK_PREFIX = "lock:";
}
