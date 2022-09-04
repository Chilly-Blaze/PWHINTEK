package com.pwhintek.backend.config;

import cn.dev33.satoken.interceptor.SaRouteInterceptor;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author ChillyBlaze
 * @version 1.0
 * @since 2022 Apr 22 22:38
 */
@EnableWebMvc
@Configuration
public class SaInterceptorConfig implements WebMvcConfigurer {

    /**
     * 前置拦截，对不符合要求的请求直接抛出异常
     *
     * @author ChillyBlaze
     * @since 24/04/2022 13:56
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SaRouteInterceptor(
                        (request, response, handler) -> {
                            SaRouter.match("/user/**").notMatch("/user/signup", "/user/login", "/user/info/**", "/user/test", "/user/my_info", "/user/gt_avatar/**").check(r -> StpUtil.checkLogin());
                            SaRouter.match("/article/**").notMatch("/article/p/**").check(r -> StpUtil.checkLogin());
                        }))
                .addPathPatterns("/**");
    }
}
