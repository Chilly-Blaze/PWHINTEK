package com.pwhintek.backend.exception.article;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;

import java.util.Map;

import static com.pwhintek.backend.constant.ArticleConstants.UPDATE_INFO;
import static com.pwhintek.backend.constant.GlobalConstants.UPDATE_ERROR;
import static com.pwhintek.backend.constant.ArticleConstants.VERIFY_ERROR;

/**
 * 更新内容非法异常
 *
 * @author ChillyBlaze
 * @version 1.0
 * @since 2022 May 30 22:12
 */
public class ArticleUpdateFailException extends ArticleException {

    /**
     * 主异常
     *
     * @param message 错误信息
     * @param data    请求原因
     */
    public ArticleUpdateFailException(String message, String data) {
        super(message, data);
    }

    public static ArticleUpdateFailException getInstance(Map<String, String> data) {
        String s = JSONUtil.toJsonStr(data);
        return new ArticleUpdateFailException(UPDATE_ERROR, s);
    }

    public static ArticleUpdateFailException getVerifyInstance(Map<String, String> data) {
        String s = JSONUtil.toJsonStr(data);
        return new ArticleUpdateFailException(StrUtil.format(VERIFY_ERROR, UPDATE_INFO), s);
    }
}
