package com.pwhintek.backend.exception.article;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.json.JSONUtil;
import com.pwhintek.backend.dto.ArticleDTO;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;

/**
 * 文章相关异常类
 *
 * @author ChillyBlaze
 * @version 1.0
 * @since 2022 May 30 19:59
 */
@Getter
public class ArticleException extends RuntimeException {
    private final String id;
    private final String data;

    /**
     * 主异常
     *
     * @param message 错误信息
     * @param data    请求原因
     */
    public ArticleException(String message, String data) {
        super(message);
        if (StpUtil.isLogin())
            this.id = StpUtil.getLoginIdAsString();
        else
            this.id = "-1";
        this.data = data;
    }
}
