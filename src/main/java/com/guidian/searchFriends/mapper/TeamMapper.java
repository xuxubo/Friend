package com.guidian.searchFriends.mapper;

import com.guidian.searchFriends.model.Team;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author 27605
* @description 针对表【team(队伍)】的数据库操作Mapper
* @createDate 2024-09-05 18:52:34
* @Entity com.guidian.searchFriends.model.Team
*/
@Mapper
public interface TeamMapper extends BaseMapper<Team> {

    List<Team> getJoinTeam(@Param("userId") Long userId);

}




