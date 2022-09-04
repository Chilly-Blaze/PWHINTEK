package com.pwhintek.backend.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 详细信息传输，不包含id，密码和逻辑删除
 *
 * @author ChillyBlaze
 * @version 1.0
 * @since 2022 Apr 23 21:47
 */
@Data
public class DetailedUserInfoDTO implements Serializable {
    // TODO: 待定，是否加入id
    // private String id;
    private String username;
    private String nickname;
    private String permission;
    private Date createTime;
    private String avatar;
}
