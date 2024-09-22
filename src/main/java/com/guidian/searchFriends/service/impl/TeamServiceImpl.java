package com.guidian.searchFriends.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.guidian.searchFriends.model.Team;
import com.guidian.searchFriends.service.TeamService;
import com.guidian.searchFriends.mapper.TeamMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author 27605
* @description 针对表【team(队伍)】的数据库操作Service实现
* @createDate 2024-09-05 16:57:13
*/
@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team>
    implements TeamService{

    @Autowired
    private TeamMapper teamMapper;

    @Override
    public List<Team> getByUserId(Long id) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("userId", id);
        List<Team> team = teamMapper.selectList(queryWrapper);
        return team;
    }

    public List<Team> getJoinTeam(Long id) {
        List<Team> teams = teamMapper.getJoinTeam(id);
        return teams;
    }
}




