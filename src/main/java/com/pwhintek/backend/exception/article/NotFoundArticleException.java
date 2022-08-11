package com.pwhintek.backend.exception.article;


import static com.pwhintek.backend.constant.ArticleConstants.ARTICLE_NOT_FOUND_ERROR;
import static com.pwhintek.backend.constant.ArticleConstants.PAGE_NOT_FOUND_ERROR;

/**
 * 指定页面超出范围
 *
 * @author ChillyBlaze
 * @version 1.0
 * @since 2022 May 30 20:07
 */
public class NotFoundArticleException extends ArticleException {
    public NotFoundArticleException(String message, String data) {
        super(message, data);
    }

    static public NotFoundArticleException getPageInstance(String data) {
        return new NotFoundArticleException(PAGE_NOT_FOUND_ERROR, data);
    }

    static public NotFoundArticleException getInstance(String data) {
        return new NotFoundArticleException(ARTICLE_NOT_FOUND_ERROR, data);
    }
}
