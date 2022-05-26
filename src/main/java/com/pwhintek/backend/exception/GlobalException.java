package com.pwhintek.backend.exception;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.SaTokenException;
import com.pwhintek.backend.dto.Result;
import com.pwhintek.backend.exception.userinfo.UserInfoException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.pwhintek.backend.constant.UserInfoConstants.NO_LOGIN_ERROR;

/**
 * 全局异常拦截
 *
 * @author ChillyBlaze
 * @version 1.0
 * @since 2022 Apr 22 16:21
 */
@Slf4j
@RestControllerAdvice
public class GlobalException {

    @ExceptionHandler(NotLoginException.class)
    public Result handleNoLoginException(NotLoginException e) {
        log.error("{}\n{}", e.getClass(), e.getMessage());
        return Result.fail(NO_LOGIN_ERROR);
    }

    /**
     * 返回父级权限问题
     *
     * @author ChillyBlaze
     * @since 23/04/2022 21:14
     */
    @ExceptionHandler(SaTokenException.class)
    public Result handleSaTokenException(SaTokenException e) {
        log.error("{}\n{}", e.getClass(), e.getMessage());
        return Result.fail(e.getMessage());
    }

    /**
     * 捕获输入信息错误异常，返回信息
     *
     * @author ChillyBlaze
     * @since 22/04/2022 19:32
     */
    @ExceptionHandler(UserInfoException.class)
    public Result handleUserInfoException(UserInfoException e) {
        // TODO: 或许可以将错误的记录进行静态存储记录,发现一些不法用户恶意请求
        log.error("{}\n{}\n{}", e.getClass(), e.getMessage(), e.getErrorData());
        return Result.fail(e.getMessage());
    }

    /**
     * 其他运行时异常捕获
     *
     * @author ChillyBlaze
     * @since 22/04/2022 19:33
     */
    @ExceptionHandler(RuntimeException.class)
    public Result handleRuntimeException(RuntimeException e) {
        log.error(e.toString(), e.getMessage());
        return Result.fail("奇怪的异常");
    }
}
