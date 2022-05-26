package com.pwhintek.backend.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 逻辑过期存储形式
 *
 * @author ChillyBlaze
 * @version 1.0
 * @since 2022 Apr 22 20:41
 */
@Data
public class RedisData {
    private LocalDateTime expireTime;
    private Object data;
}
