package com.pwhintek.backend.service;

import com.github.yulichang.base.MPJBaseService;
import com.pwhintek.backend.dto.Result;
import com.pwhintek.backend.entity.Article;

import java.util.Map;

/**
 * @author chillyblaze
 * 针对表【t_article(用户文章表)】的数据库操作Service
 * @since 2022-05-28 11:56:11
 */
public interface ArticleService extends MPJBaseService<Article> {
    Result pageSelectById(Integer num, String id);

    Result pageSelect(Integer num);

    Result updateArticle(Map<String, String> map);

    Result addOrRemoveLike(Long id);

    Result insertArticle(Map<String, String> map);

    Result deleteArticle(Long id);
}
