package com.pwhintek.backend.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author ChillyBlaze
 * @version 1.0
 * @since 2022 May 29 22:14
 */
@Data
public class ArticleDTO implements Serializable {
    /**
     * 文章id
     */
    private Long id;

    /**
     * 文章标题
     */
    private String title;

    /**
     * 文章简略描述
     */
    private String description;

    /**
     * 文章内容
     */
    private String content;

    /**
     * 文章存储类型
     */
    private String type;

    /**
     * 收藏数
     */
    private Long likeNum;

    /**
     * 创建时间
     */
    private Date createTime;
}
