package com.guidian.searchFriends.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.guidian.searchFriends.model.UserTeam;
import com.guidian.searchFriends.service.UserTeamService;
import com.guidian.searchFriends.mapper.UserTeamMapper;
import org.springframework.stereotype.Service;

/**
* @author Administrator
* @description 针对表【user_team(用户队伍关系)】的数据库操作Service实现
* @createDate 2024-10-05 09:34:41
*/
@Service
public class UserTeamServiceImpl extends ServiceImpl<UserTeamMapper, UserTeam>
    implements UserTeamService{

}




