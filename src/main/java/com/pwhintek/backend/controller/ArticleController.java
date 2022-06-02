package com.pwhintek.backend.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.pwhintek.backend.dto.Result;
import com.pwhintek.backend.service.ArticleService;
import com.pwhintek.backend.utils.RedisStorageSolution;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
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

    /**
     * 获取个人文章页面
     *
     * @param num 文章页号
     */
    @GetMapping("/my/{num}")
    public Result myArticle(@PathVariable("num") Integer num) {
        String id = StpUtil.getLoginIdAsString();
        return articleService.pageSelectById(num, id);
    }

    /**
     * 获取最新发布文章页面
     *
     * @param num 文章页号
     */
    @GetMapping("/p/{num}")
    public Result newArticle(@PathVariable("num") Integer num) {
        return articleService.pageSelect(num);
    }

    /**
     * 更新文章信息
     *
     * @param map 文章内容映射集，比如包含id，title，
     */
    @PostMapping("/update")
    public Result updateArticle(@RequestBody Map<String, String> map) {
        return articleService.updateArticle(map);
    }

    /**
     * 点赞/取消点赞行为
     *
     * @param id 文章id
     */
    @PostMapping("/like/{id}")
    public Result addRemoveLike(@PathVariable("id") Long id) {
        return articleService.addOrRemoveLike(id);
    }

    /**
     * 插入文章
     *
     * @param map 文章内容
     */
    @PostMapping("/insert")
    public Result addArticle(@RequestBody Map<String, String> map) {
        return articleService.insertArticle(map);
    }

    /**
     * 获取特定用户文章页面
     *
     * @param num 文章页号
     * @param id  查询用户id
     */
    @GetMapping("/p/{id}/{num}")
    public Result userArticle(@PathVariable("num") Integer num, @PathVariable("id") Long id) {
        return articleService.pageSelectById(num, id.toString());
    }

    @PostMapping("/delete/{id}")
    public Result deleteArticle(@PathVariable("id") Long id) {
        return articleService.deleteArticle(id);
    }
}
