package com.pwhintek.backend.mapper;

import com.pwhintek.backend.entity.LikeMapping;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author chillyblaze
 * 针对表【t_like_mapping(个人和点赞文章映射关系)】的数据库操作Mapper
 * @since 2022-06-01 14:45:01
 * com.pwhintek.backend.entity.LikeMapping
 */
@Mapper
public interface LikeMappingMapper extends BaseMapper<LikeMapping> {

}




