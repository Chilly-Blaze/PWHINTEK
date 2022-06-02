package com.pwhintek.backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pwhintek.backend.entity.LikeMapping;
import com.pwhintek.backend.service.LikeMappingService;
import com.pwhintek.backend.mapper.LikeMappingMapper;
import org.springframework.stereotype.Service;

/**
 * @author chillyblaze
 * 针对表【t_like_mapping(个人和点赞文章映射关系)】的数据库操作Service实现
 * @since 2022-06-01 14:45:01
 */
@Service
public class LikeMappingServiceImpl extends ServiceImpl<LikeMappingMapper, LikeMapping>
        implements LikeMappingService {

}




