package com.pwhintek.backend.mapper;

import com.github.yulichang.base.MPJBaseMapper;
import com.pwhintek.backend.entity.Article;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author chillyblaze
 * 针对表【t_article(用户文章表)】的数据库操作Mapper
 * @since 2022-05-28 11:56:11
 */
@Mapper
public interface ArticleMapper extends MPJBaseMapper<Article> {

}




