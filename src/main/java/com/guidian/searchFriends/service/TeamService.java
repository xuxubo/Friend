package com.guidian.searchFriends.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.guidian.searchFriends.model.Team;
import com.baomidou.mybatisplus.extension.service.IService;
import com.guidian.searchFriends.model.User;
import com.guidian.searchFriends.model.dto.TeamQuery;
import com.guidian.searchFriends.model.request.TeamAddRequest;
import com.guidian.searchFriends.model.request.TeamJoinRequest;
import com.guidian.searchFriends.model.request.TeamQuiteRequest;
import com.guidian.searchFriends.model.request.TeamUpdateRequest;
import com.guidian.searchFriends.model.vo.TeamUserVO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
* @author 27605
* @description 针对表【team(队伍)】的数据库操作Service
* @createDate 2024-09-05 16:57:13
*/
public interface TeamService extends IService<Team> {

    List<Team> getByUserId(Long id);

    List<Team> getJoinTeam(Long id);


    long addTeam(TeamAddRequest team, User loginUser);


    List<Team> listTeams(String searchText, Integer status, Integer pageNum, boolean isAdmin);

    boolean updateTeam(TeamUpdateRequest teamUpdateRequest, User loginUser);

    boolean joinTeam(TeamJoinRequest teamJoinRequest, User loginUser);

    @Transactional(rollbackFor = Exception.class)
    boolean quieTeam(TeamQuiteRequest teamQuiteRequest, User loginUser);

    @Transactional(rollbackFor = Exception.class)
    boolean deleteTeam(Long id, User loginUser);
}
