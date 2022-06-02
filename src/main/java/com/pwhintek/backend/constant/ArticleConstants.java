package com.pwhintek.backend.constant;

import cn.hutool.core.collection.ListUtil;

import java.util.Arrays;
import java.util.List;

/**
 * 文章相关常量
 *
 * @author ChillyBlaze
 * @version 1.0
 * @since 2022 May 28 11:56
 */
public class ArticleConstants {
    public static final String DATABASE_A_ID = "article_id";
    public static final String DATABASE_A_TITLE = "title";
    public static final String DATABASE_A_DESCRIPTION = "description";
    public static final String DATABASE_A_CONTENT = "content";
    public static final String DATABASE_A_TYPE = "type";
    public static final String DATABASE_A_LIKE = "like_num";
    public static final String DATABASE_A_UID = "user_id";
    public static final String DATABASE_A_CREATE_TIME = "create_time";
    public static final String DATABASE_A_IS_DELETE = "is_delete";

    public static final Integer PAGE_SIZE = 3;

    public static final String UPDATE_INFO = "更新";
    public static final String DELETE_INFO = "删除";
    public static final String ARTICLE_NOT_FOUND_ERROR = "你想对虚空文章做什么操作吗！";
    public static final String PAGE_NOT_FOUND_ERROR = "再怎么找也没有啦！";
    public static final String VERIFY_ERROR = "大胆！居然想{}别人的文章！";

    public static final String REMOVE_LIKE_INFO = "取消点赞成功";
    public static final String ADD_LIKE_INFO = "点赞成功";

    public static final List<String> A_ALLOW_UPDATE = Arrays.asList("id", "title", "description", "content", "type");
    public static final List<String> A_ALLOW_INSERT = ListUtil.sub(A_ALLOW_UPDATE, 1, A_ALLOW_UPDATE.size());
}
