package com.pwhintek.backend.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.pwhintek.backend.dto.Result;
import com.pwhintek.backend.entity.Article;
import com.pwhintek.backend.service.ArticleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 文章查询API
 *
 * @author ChillyBlaze
 * @version 1.0
 * @since 2022 May 28 18:05
 */
@Slf4j
@RestController
@RequestMapping("/article")
public class ArticleController {

    @Resource
    private ArticleService articleService;

    @GetMapping("/my/{num}")
    public Result myArti(@PathVariable("num") Integer num) {
        String id = StpUtil.getLoginIdAsString();
        return articleService.pageSelectById(num, id);
    }

}
