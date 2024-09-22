package com.guidian.searchFriends.mapper;

import com.guidian.searchFriends.model.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author 27605
* @description 针对表【user(用户)】的数据库操作Mapper
* @createDate 2024-05-13 22:04:26
* @Entity com.guidian.searchFriends.model.User
*/
@Mapper
public interface UserMapper extends BaseMapper<User> {

}




