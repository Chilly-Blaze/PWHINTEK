package com.pwhintek.backend.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

import lombok.Data;

import static com.pwhintek.backend.constant.UserInfoConstants.DATABASE_ID;

/**
 * 用户信息表
 * t_user
 */
@Data
public class User implements Serializable {
    /**
     * 用户唯一标识
     */
    @TableId(DATABASE_ID)
    private Long id;

    /**
     * 唯一用户名
     */
    private String username;

    /**
     * 加密密码
     */
    private String password;

    /**
     * 用户昵称
     */
    private String nickname;

    /**
     * 用户权限组
     */
    private String permission;

    /**
     * 用户创建时间
     */
    private Date createTime;

    /**
     * 用户头像
     */
    private String portrait;

    /**
     * 逻辑删除
     */
    @TableLogic
    private Integer isDelete;

    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}