package com.pwhintek.backend.exception.article;

import cn.hutool.json.JSONUtil;
import com.pwhintek.backend.entity.Article;

import static com.pwhintek.backend.constant.GlobalConstants.DUPLICATION_UPDATE;

/**
 * @author ChillyBlaze
 * @version 1.0
 * @since 2022 May 30 22:52
 */
public class ArticleIdempotenceException extends ArticleException {

    /**
     * 主异常
     *
     * @param message 错误信息
     * @param data    请求原因
     */
    public ArticleIdempotenceException(String message, String data) {
        super(message, data);
    }

    public static ArticleIdempotenceException getInstance(Article data) {
        String s = JSONUtil.toJsonStr(data);
        return new ArticleIdempotenceException(DUPLICATION_UPDATE, s);
    }
}
