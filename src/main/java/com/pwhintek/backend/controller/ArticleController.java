package com.pwhintek.backend.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.pwhintek.backend.dto.Result;
import com.pwhintek.backend.service.ArticleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;


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
    @Autowired
    private HttpServletRequest request;

    @GetMapping("/my/{num}")
    public Result myArticle(@PathVariable("num") Integer num) {
        String id = StpUtil.getLoginIdAsString();
        return articleService.pageSelectById(num, id);
    }

    @GetMapping("/{num}")
    public Result newArticle(@PathVariable("num") Integer num) {
        return articleService.pageSelect(num);
    }

    @GetMapping("/update")
    public Result updateArticle(@RequestBody Map<String, String> map) {
        return articleService.updateArticle(map);
    }

    // TODO: 增加点赞数
    // TODO: 插入文章内容
    // TODO: 查找指定id文章
    // TODO: 删除文章
}
