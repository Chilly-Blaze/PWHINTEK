package com.pwhintek.backend.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.NumberUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseMapper;
import com.github.yulichang.base.MPJBaseServiceImpl;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.pwhintek.backend.dto.ArticleDTO;
import com.pwhintek.backend.dto.DetailedArticleDTO;
import com.pwhintek.backend.dto.Result;
import com.pwhintek.backend.entity.Article;
import com.pwhintek.backend.entity.LikeMapping;
import com.pwhintek.backend.entity.User;
import com.pwhintek.backend.exception.article.ArticleDeleteFailException;
import com.pwhintek.backend.exception.article.ArticleIdempotenceException;
import com.pwhintek.backend.exception.article.NotFoundArticleException;
import com.pwhintek.backend.exception.article.ArticleUpdateFailException;
import com.pwhintek.backend.mapper.ArticleMapper;
import com.pwhintek.backend.service.ArticleService;
import com.pwhintek.backend.service.LikeMappingService;
import com.pwhintek.backend.utils.MiscUtils;
import com.pwhintek.backend.utils.RedisStorageSolution;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static com.pwhintek.backend.constant.ArticleConstants.*;
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
    private ArticleMapper articleMapper;

    @Resource
    private LikeMappingService likeMappingService;

    @Resource
    private RedisStorageSolution redisStorageSolution;

    @Override
    public Result pageSelectById(Integer num, String id) {
        // id查找条件
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<Article>().select(Article::getId).eq(Article::getUid, id).orderByDesc(Article::getCreateTime);
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
        // 保证传入信息正确
        if (MiscUtils.isNotAllInList(A_ALLOW_UPDATE, map))
            throw ArticleUpdateFailException.getInstance(map);
        // 保证更新文章存在
        String id = map.get("id");
        String uid = StpUtil.getLoginIdAsString();
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<Article>().select(Article::getUid).eq(Article::getId, id);
        if (!uid.equals(getOne(wrapper).getUid().toString())) {
            throw ArticleUpdateFailException.getVerifyInstance(map);
        }
        // 封装更新数据
        Article article = BeanUtil.fillBeanWithMap(map, new Article(), true);
        article.setUid(Long.valueOf(uid));
        // 获取互斥锁
        String lockKey = ARTICLE_PREFIX + LOCK_PREFIX + uid;
        String articleKey = ARTICLE_PREFIX + INFO_PREFIX + id;
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

    @Override
    public Result addOrRemoveLike(Long id) {
        // 校验
        // 文章是否存在
        if (NumberUtil.equals(count(new LambdaQueryWrapper<Article>().eq(Article::getId, "1")), 0)) {
            // 不存在，抛出异常
            throw NotFoundArticleException.getInstance(id.toString());
        }
        // 获取登录用户id
        Long uid = StpUtil.getLoginIdAsLong();
        String lockKey = ARTICLE_PREFIX + LOCK_PREFIX + uid;
        // 保证用户操作幂等性
        if (!redisStorageSolution.tryLock(lockKey)) {
            throw ArticleIdempotenceException.getInstance(id.toString());
        }
        // 调用queryWithPassThrough，保证Redis中有当前点赞记录
        Function<Long, Long> c = r -> likeMappingService.count(new LambdaQueryWrapper<LikeMapping>()
                .eq(LikeMapping::getArticleId, r));
        redisStorageSolution.queryWithPassThrough(ARTICLE_PREFIX + LIKE_COUNT_PREFIX, id, c, ARTICLE_TTL, TimeUnit.DAYS);
        // 是否已经点过赞，直接调用删除操作查看返回值
        String key = ARTICLE_PREFIX + LIKE_COUNT_PREFIX + id;
        boolean isOk = likeMappingService.remove(new LambdaQueryWrapper<LikeMapping>().eq(LikeMapping::getArticleId, id).eq(LikeMapping::getUserId, uid));
        if (isOk) {
            redisStorageSolution.decreaseKey(key);
            redisStorageSolution.unlock(lockKey);
            return Result.ok(REMOVE_LIKE_INFO);
        }
        // 未点过，添加数据库内容
        LikeMapping entity = new LikeMapping();
        entity.setArticleId(id);
        entity.setUserId(uid);
        // 更新数据
        likeMappingService.save(entity);
        redisStorageSolution.increaseKey(key);
        redisStorageSolution.unlock(lockKey);
        return Result.ok(ADD_LIKE_INFO);
    }

    @Override
    public Result insertArticle(Map<String, String> map) {
        // 不符合插入要求
        if (MiscUtils.isNotAllInList(A_ALLOW_INSERT, map)) throw ArticleUpdateFailException.getInstance(map);
        // 封装文章信息
        String uid = StpUtil.getLoginIdAsString();
        Article article = BeanUtil.fillBeanWithMap(map, new Article(), true);
        article.setUid(Long.valueOf(uid));
        // 保证幂等性
        String lockKey = ARTICLE_PREFIX + LOCK_PREFIX + uid;
        if (!redisStorageSolution.tryLock(lockKey)) {
            throw ArticleIdempotenceException.getInstance(article);
        }
        // 插入文章
        save(article);
        // 释放锁
        redisStorageSolution.unlock(lockKey);
        return Result.ok();
    }

    @Override
    public Result deleteArticle(Long id) {
        // 判断给定文章id是否正确
        // 文章是否存在
        if (NumberUtil.equals(count(new LambdaQueryWrapper<Article>().eq(Article::getId, id)), 0)) {
            // 不存在，抛出异常
            throw NotFoundArticleException.getInstance(id.toString());
        }
        // 文章作者正误
        String uid = StpUtil.getLoginIdAsString();
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<Article>().select(Article::getUid).eq(Article::getId, id);
        if (!uid.equals(getOne(wrapper).getUid().toString())) {
            throw ArticleDeleteFailException.getInstance();
        }
        // 逻辑删除内容
        removeById(id);
        return Result.ok();
    }

    private List<DetailedArticleDTO> idFunction2ArticleList(Integer num, LambdaQueryWrapper<Article> wrapper) {
        // 页面初始化
        Page<Article> page = new Page<>(num, PAGE_SIZE);
        // 寻找文章idList
        List<Article> records = page(page, wrapper).getRecords();
        if (records.isEmpty()) throw NotFoundArticleException.getPageInstance(num.toString());
        // 根据文章id查找返回DetailArticleDTO内容
        Function<Long, DetailedArticleDTO> f = r -> selectJoinOne(DetailedArticleDTO.class, new MPJLambdaWrapper<Article>()
                .selectAll(Article.class)
                .selectAs(Article::getId, DetailedArticleDTO::getId)
                .selectAs(User::getId, DetailedArticleDTO::getUid)
                .select(User::getUsername, User::getNickname, User::getPortrait)
                .leftJoin(User.class, User::getId, Article::getUid)
                .eq(Article::getId, r));
        Function<Long, Long> c = r -> likeMappingService.count(new LambdaQueryWrapper<LikeMapping>()
                .eq(LikeMapping::getArticleId, r));
        // 返回文章内容列表
        List<DetailedArticleDTO> ArticleList = new ArrayList<>();
        for (Article record : records) {
            DetailedArticleDTO dto = redisStorageSolution.queryWithPassThrough(
                    ARTICLE_PREFIX + INFO_PREFIX,
                    record.getId(),
                    DetailedArticleDTO.class,
                    f,
                    ARTICLE_TTL,
                    TimeUnit.DAYS);
            dto.setLikeNum(redisStorageSolution.queryWithPassThrough(
                    ARTICLE_PREFIX + LIKE_COUNT_PREFIX,
                    record.getId(),
                    c,
                    ARTICLE_TTL,
                    TimeUnit.DAYS));
            ArticleList.add(dto);
        }
        return ArticleList;
    }

}




