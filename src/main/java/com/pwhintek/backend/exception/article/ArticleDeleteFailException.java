package com.pwhintek.backend.exception.article;

import cn.hutool.core.util.StrUtil;

import static com.pwhintek.backend.constant.ArticleConstants.DELETE_INFO;
import static com.pwhintek.backend.constant.ArticleConstants.VERIFY_ERROR;

/**
 * @author ChillyBlaze
 * @version 1.0
 * @since 2022 Jun 02 22:41
 */
public class ArticleDeleteFailException extends ArticleException {

    /**
     * 主异常
     *
     * @param message 错误信息
     * @param data    请求原因
     */
    public ArticleDeleteFailException(String message, String data) {
        super(message, data);
    }

    public static ArticleDeleteFailException getInstance() {
        return new ArticleDeleteFailException(StrUtil.format(VERIFY_ERROR, DELETE_INFO), null);
    }
}
