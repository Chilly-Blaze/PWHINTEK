package com.pwhintek.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * 个人和点赞文章映射关系
 * t_like_mapping
 */
@Data
public class LikeMapping implements Serializable {
    /**
     * 文章编号
     */
    private Long articleId;

    /**
     * 用户编号
     */
    private Long userId;

    /**
     * 创建时间
     */
    private Date createTime;

    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}