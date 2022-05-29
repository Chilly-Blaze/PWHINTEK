package com.pwhintek.backend.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

import static com.pwhintek.backend.constant.ArticleConstants.DATABASE_A_ID;
import static com.pwhintek.backend.constant.ArticleConstants.DATABASE_A_UID;

/**
 * 用户文章表
 * t_article
 */
@Data
public class Article implements Serializable {
    /**
     * 文章id
     */
    @TableId(DATABASE_A_ID)
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
    @TableField(DATABASE_A_UID)
    private Long uid;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 逻辑删除
     */
    @TableLogic
    private Integer isDelete;

    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}