package com.pwhintek.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 返回前端信息统一封装
 *
 * @author ChillyBlaze
 * @version 1.0
 * @since 2022 Apr 22 16:30
 */
@Data
@AllArgsConstructor
public class Result implements Serializable {
    private boolean isOK;
    private String failMsg;
    private Object data;

    public static Result ok() {
        return generate(true, null, null);
    }

    public static Result ok(Object data) {
        return generate(true, null, data);
    }

    public static Result fail(String msg) {
        return generate(false, msg, null);
    }

    public static Result generate(boolean isOK, String failMsg, Object data) {
        return new Result(isOK, failMsg, data);
    }
}
