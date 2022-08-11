package com.pwhintek.backend.mapper;

import com.pwhintek.backend.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * com.pwhintek.backend.entity.User
 * 针对表【t_user(用户信息表)】的数据库操作Mapper
 *
 * @author chillyblaze
 * @since 2022-04-22 11:07:13
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

}




