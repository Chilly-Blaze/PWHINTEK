package com.pwhintek.backend.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseMapper;
import com.github.yulichang.base.MPJBaseServiceImpl;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.pwhintek.backend.dto.ArticleDTO;
import com.pwhintek.backend.dto.DetailedArticleDTO;
import com.pwhintek.backend.dto.Result;
import com.pwhintek.backend.entity.Article;
import com.pwhintek.backend.entity.User;
import com.pwhintek.backend.exception.article.ArticleIdempotenceException;
import com.pwhintek.backend.exception.article.NotFoundPageException;
import com.pwhintek.backend.exception.article.ArticleUpdateFailException;
import com.pwhintek.backend.exception.userinfo.UserInfoIdempotenceException;
import com.pwhintek.backend.service.ArticleService;
import com.pwhintek.backend.utils.RedisStorageSolution;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static com.pwhintek.backend.constant.ArticleConstants.A_ALLOW_UPDATE;
import static com.pwhintek.backend.constant.ArticleConstants.PAGE_SIZE;
import static com.pwhintek.backend.constant.RedisConstants.*;
import static com.pwhintek.backend.constant.RedisConstants.LOCK_PREFIX;

/**
 * @author chillyblaze
 * 针对表【t_article(用户文章表)】的数据库操作Service实现
 * @since 2022-05-28 11:56:11
 */
@Service
@AllArgsConstructor
public class ArticleServiceImpl extends MPJBaseServiceImpl<MPJBaseMapper<Article>, Article>
        implements ArticleService {

    @Resource
    private RedisStorageSolution redisStorageSolution;

    // TODO: 查询热点数据，每几分钟刷新一次，同时通过范围内逻辑存储，先判断超时，后台替换redis数据
    @Override
    public Result pageSelectById(Integer num, String id) {
        // id查找条件
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<Article>().select(Article::getId).eq(Article::getUid, id);
        // 将给定id查找条件和页号，转为DetailedArticleDTO
        List<DetailedArticleDTO> ArticleList = idFunction2ArticleList(num, wrapper);
        // 将DetailedArticleDTO转为ArticleDTO
        ArrayList<ArticleDTO> articleDTOS = new ArrayList<>();
        for (DetailedArticleDTO articleDTO : ArticleList) {
            articleDTOS.add(BeanUtil.copyProperties(articleDTO, ArticleDTO.class));
        }
        return Result.ok(articleDTOS);
    }

    @Override
    public Result pageSelect(Integer num) {
        // id查找条件
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<Article>().select(Article::getId).orderByDesc(Article::getCreateTime);
        // 将给定id查找条件和页号，转为DetailedArticleDTO
        return Result.ok(idFunction2ArticleList(num, wrapper));
    }

    @Override
    public Result updateArticle(Map<String, String> map) {
        // 检查传入内容是否符合要求（id和key）
        Set<String> set = map.keySet();
        for (String s : set) {
            if (!A_ALLOW_UPDATE.contains(s)) {
                throw ArticleUpdateFailException.getInstance(map);
            }
        }
        String id = map.get("id");
        String uid = StpUtil.getLoginIdAsString();
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<Article>().select(Article::getUid).eq(Article::getId, id);
        if (!uid.equals(getOne(wrapper).getUid().toString())) {
            throw ArticleUpdateFailException.getInstance(map);
        }
        // 封装更新数据
        Article article = BeanUtil.fillBeanWithMap(map, new Article(), true);
        article.setUid(Long.valueOf(uid));
        // 获取互斥锁
        String lockKey = ARTICLE_PREFIX + LOCK_PREFIX + id;
        String articleKey = ARTICLE_PREFIX + id;
        // 保证更新操作幂等
        if (!redisStorageSolution.tryLock(lockKey)) {
            throw ArticleIdempotenceException.getInstance(article);
        }
        // 更新数据库
        updateById(article);
        // 删除Redis缓存
        redisStorageSolution.deleteByKey(articleKey);
        // 释放锁
        redisStorageSolution.unlock(lockKey);
        return Result.ok();
    }

    private List<DetailedArticleDTO> idFunction2ArticleList(Integer num, LambdaQueryWrapper<Article> wrapper) {
        // 页面初始化
        Page<Article> page = new Page<>(num, PAGE_SIZE);
        // 寻找文章idList
        List<Article> records = page(page, wrapper).getRecords();
        if (records.isEmpty()) throw NotFoundPageException.getInstance(num.toString());
        // 根据文章id查找返回DetailArticleDTO内容
        Function<Long, DetailedArticleDTO> f = r -> selectJoinOne(DetailedArticleDTO.class, new MPJLambdaWrapper<Article>()
                .selectAll(Article.class)
                .selectAs(Article::getId, DetailedArticleDTO::getId)
                .selectAs(User::getId, DetailedArticleDTO::getUid)
                .select(User::getUsername, User::getNickname, User::getPortrait)
                .leftJoin(User.class, User::getId, Article::getUid)
                .eq(Article::getId, r));
        // 返回文章内容列表
        List<DetailedArticleDTO> ArticleList = new ArrayList<>();
        for (Article record : records) {
            ArticleList.add(redisStorageSolution.queryWithPassThrough(
                    ARTICLE_PREFIX,
                    record.getId(),
                    DetailedArticleDTO.class,
                    f,
                    ARTICLE_TTL,
                    TimeUnit.DAYS));
        }
        return ArticleList;
    }
}




