package com.pwhintek.backend.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 前端提交的表单转换
 *
 * @author ChillyBlaze
 * @version 1.0
 * @since 22/04/2022
 */
@Data
public class SignDTO implements Serializable {
    private String username;
    private String password;
    private String nickname;
}
