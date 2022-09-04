package com.pwhintek.backend.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * @author ChillyBlaze
 * @version 1.0
 * @since 2022 May 29 18:49
 */
@Data
public class DetailedArticleDTO implements Serializable {
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
     * 作者
     */
    private Long uid;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 唯一用户名
     */
    private String username;

    /**
     * 用户昵称
     */
    private String nickname;

    /**
     * 用户头像
     */
    private String avatar;

    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
