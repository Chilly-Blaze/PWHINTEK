package com.pwhintek.backend.exception.article;


import ch.qos.logback.classic.spi.IThrowableProxy;
import com.pwhintek.backend.dto.ArticleDTO;

import static com.pwhintek.backend.constant.ArticleConstants.PAGE_NOT_FOUND_ERROR;

/**
 * 指定页面超出范围
 *
 * @author ChillyBlaze
 * @version 1.0
 * @since 2022 May 30 20:07
 */
public class NotFoundPageException extends ArticleException {
    public NotFoundPageException(String message, String data) {
        super(message, data);
    }

    static public NotFoundPageException getInstance(String data) {
        return new NotFoundPageException(PAGE_NOT_FOUND_ERROR, data);
    }
}
