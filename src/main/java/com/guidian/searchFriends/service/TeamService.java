package com.guidian.searchFriends.service;

import com.guidian.searchFriends.model.Team;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author 27605
* @description 针对表【team(队伍)】的数据库操作Service
* @createDate 2024-09-05 16:57:13
*/
public interface TeamService extends IService<Team> {

    List<Team> getByUserId(Long id);

    List<Team> getJoinTeam(Long id);
}
