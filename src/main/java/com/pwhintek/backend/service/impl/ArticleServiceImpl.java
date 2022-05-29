package com.pwhintek.backend.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.yulichang.base.MPJBaseMapper;
import com.github.yulichang.base.MPJBaseServiceImpl;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.pwhintek.backend.dto.ArticleDTO;
import com.pwhintek.backend.dto.DetailedArticleDTO;
import com.pwhintek.backend.dto.Result;
import com.pwhintek.backend.entity.Article;
import com.pwhintek.backend.entity.User;
import com.pwhintek.backend.service.ArticleService;
import com.pwhintek.backend.mapper.ArticleMapper;
import com.pwhintek.backend.utils.RedisStorageSolution;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.util.annotation.Nullable;

import javax.annotation.Resource;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static com.pwhintek.backend.constant.ArticleConstants.PAGE_SIZE;
import static com.pwhintek.backend.constant.RedisConstants.ARTICLE_PREFIX;
import static com.pwhintek.backend.constant.RedisConstants.ARTICLE_TTL;

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
        // 页面初始化
        Page<Article> page = new Page<>(num, PAGE_SIZE);
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<Article>().select(Article::getId).eq(Article::getUid, id);
        // 根据uid寻找文章idList
        List<Article> records = page(page, wrapper).getRecords();
        // 根据文章id查找返回DetailArticleDTO内容
        Function<Long, DetailedArticleDTO> f = r -> selectJoinOne(DetailedArticleDTO.class, new MPJLambdaWrapper<Article>()
                .selectAll(Article.class)
                .selectAs(Article::getId, DetailedArticleDTO::getId)
                .selectAs(User::getId, DetailedArticleDTO::getUid)
                .select(User::getUsername, User::getNickname, User::getPortrait)
                .leftJoin(User.class, User::getId, Article::getUid)
                .eq(Article::getId, r));
        // 存储返回文章内容列表
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
        ArrayList<ArticleDTO> articleDTOS = new ArrayList<>();
        for (DetailedArticleDTO articleDTO : ArticleList) {
            articleDTOS.add(BeanUtil.copyProperties(articleDTO, ArticleDTO.class));
        }
        return Result.ok(articleDTOS);
    }
}




