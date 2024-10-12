package com.guidian.searchFriends.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.guidian.searchFriends.exception.http.NotFoundException;
import com.guidian.searchFriends.model.Team;
import com.guidian.searchFriends.model.User;
import com.guidian.searchFriends.model.UserTeam;
import com.guidian.searchFriends.model.dto.PageRequest;
import com.guidian.searchFriends.model.enums.TeamStatusEnum;
import com.guidian.searchFriends.model.request.TeamAddRequest;
import com.guidian.searchFriends.model.request.TeamJoinRequest;
import com.guidian.searchFriends.model.request.TeamQuiteRequest;
import com.guidian.searchFriends.model.request.TeamUpdateRequest;
import com.guidian.searchFriends.model.vo.TeamUserVO;
import com.guidian.searchFriends.service.TeamService;
import com.guidian.searchFriends.mapper.TeamMapper;

import com.guidian.searchFriends.service.UserService;
import com.guidian.searchFriends.service.UserTeamService;

import com.guidian.searchFriends.utils.UserUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * @author 27605
 * @description 针对表【team(队伍)】的数据库操作Service实现
 * @createDate 2024-09-05 16:57:13
 */
@Service
@Transactional
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team>    implements TeamService {

    @Autowired
    private TeamMapper teamMapper;

    @Autowired
    private UserTeamService userTeamService;

    @Autowired
    private UserService userService;

    /**
     * 我创建的队伍
     * @param id
     * @return
     */
    @Override
    public List<Team> getByUserId(Long id) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("userId", id);
        List<Team> team = teamMapper.selectList(queryWrapper);
        return team;
    }

    /**
     * 加入的队伍
     * @param id
     * @return
     */
    public List<Team> getJoinTeam(Long id) {
        List<Team> teams = teamMapper.getJoinTeam(id);
        return teams;
    }



    /**
     * 添加队伍
     * @param team
     * @param loginUser
     * @return
     */
    @Override
    public long addTeam(TeamAddRequest team, User loginUser) {
        if (team == null) {
            throw new NotFoundException(20008);
        }
        if (loginUser == null) {
            throw new NotFoundException(20008);
        }
        final long userId = loginUser.getId();
        int maxNum = Optional.ofNullable(team.getMaxNum()).orElse(0);
        if (maxNum < 1 || maxNum > 20) {
            throw new NotFoundException(20008);
        }
        String name = team.getName();
        if (StringUtils.isBlank(name) || name.length() > 20) {
            throw new NotFoundException(20008);
        }
        // (3) 描述<= 512
        String description = team.getDescription();
        if (StringUtils.isNotBlank(description) && description.length() > 512) {
            throw new NotFoundException(20008);
        }
        //(4)status 是否公开，不传默认为0
        int status = Optional.ofNullable(team.getStatus()).orElse(0);
        TeamStatusEnum statusEnum = TeamStatusEnum.getEnum(status);
        if (statusEnum == null) {
            throw new NotFoundException(20008);
        }
        //(5)如果status是加密状态，一定要密码 且密码<=32
        String password = team.getPassword();
        if (TeamStatusEnum.SECRET.equals(statusEnum)) {
            if (StringUtils.isBlank(password) || password.length() > 32) {
                throw new NotFoundException(20008);
            }
        }
        //(6)超出时间 > 当前时间
        Date expireTime = team.getExpireTime();
        if (new Date().after(expireTime)) {
            throw new NotFoundException(20008);
        }
        //(7)校验用户最多创建5个队伍
        //todo 有bug。可能同时创建100个队伍
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userId);
        long hasTeamNum = this.count(queryWrapper);
        if (hasTeamNum >= 5) {
            throw new NotFoundException(20008);
        }

        Team newteam = createTeam(team);
        //4.插入队伍消息到队伍表
        newteam.setId(null);
        newteam.setUserId(userId);
        boolean result = this.save(newteam);
        Long teamId = newteam.getId();
        if (!result || teamId == null) {
            throw new NotFoundException(20008);
        }
        //5. 插入用户 ==> 队伍关系 到关系表
        UserTeam userTeam = new UserTeam();
        userTeam.setUserId(userId);
        userTeam.setTeamId(teamId);
        userTeam.setJoinTime(new Date());

        result = userTeamService.save(userTeam);
        if (!result) {
            throw new NotFoundException(20008);
        }
        return teamId;
    }

    /**
     * 通过描述查找队伍
     * @param searchText
     * @param status
     * @param pageNum
     * @param isAdmin
     * @return
     */
    @Override
    public List<Team> listTeams(String searchText, Integer status, Integer pageNum, boolean isAdmin) {
        Page<Team> page = new Page<>(pageNum, 10);
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", status);
        queryWrapper.like("description", searchText);
        IPage<Team> teamIPage = teamMapper.selectPage(page, queryWrapper);
        List<Team> teams = teamIPage.getRecords();
        if (CollectionUtils.isEmpty(teams)) {
            return List.of();
        }
        return teams;
    }

    /**
     * 查找队伍
     * @param teamQuery
     * @param isAdmin
     * @return
     */
//    @Override
//    public List<TeamUserVO> listTeams(TeamQuery teamQuery, boolean isAdmin) {
//        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
//
//        if (teamQuery != null) {
//            Long id = teamQuery.getId();
//            if (id != null) {
//                queryWrapper.eq("id", id);
//            }
//            String name = teamQuery.getName();
//            if (StringUtils.isNotBlank(name)) {
//                queryWrapper.like("name", name);
//            }
//            String description = teamQuery.getDescription();
//            if (StringUtils.isNotBlank(description)) {
//                queryWrapper.like("description", description);
//            }
//            Integer maxNum = teamQuery.getMaxNum();
//            if (maxNum != null && maxNum > 0) {
//                queryWrapper.eq("maxNum", maxNum);
//            }
//            Integer status = teamQuery.getStatus();
//            TeamStatusEnum statusEnum = TeamStatusEnum.getEnum(status);
//            if (statusEnum == null) {
//                statusEnum = TeamStatusEnum.PUBLIC;
//            }
//            Long userId = teamQuery.getUserId();
//            if (userId != null) {
//                queryWrapper.eq("userId", userId);
//            }
//            if (!isAdmin && statusEnum != TeamStatusEnum.PUBLIC) {
//                throw new NotFoundException(20008);
//            }
//            queryWrapper.eq("status", statusEnum.getValue());
//            //不展示已过期的队伍
//            //expireTime is null or expireTime > now()
//        }
//        queryWrapper.and(qw -> qw.gt("expireTime", new Date()).or().isNull("expireTime"));
//
//        List<Team> teamList = this.list(queryWrapper);
//        if (teamList == null) {
//            return new ArrayList<>();
//        }
//        List<TeamUserVO> teamUserVOList = new ArrayList<>();
//        for (Team team : teamList) {
//            Long userId = team.getUserId();
//            if (userId == null) {
//                continue;
//            }
//            User user = userService.getById(userId);
//            TeamUserVO teamUserVO = new TeamUserVO();
//            BeanUtils.copyProperties(team, teamUserVO);
//            if (user != null) {
//                UserVO userVO = new UserVO();
//                BeanUtils.copyProperties(user, userVO);
//                teamUserVO.setCreatedUser(userVO);
//            }
//            teamUserVOList.add(teamUserVO);
//        }
//        return teamUserVOList;
//    }

    /**
     * 跟新队伍
     * @param teamUpdateRequest
     * @param loginUser
     * @return
     */
    //todo 更新队伍失败
    @Override
    public boolean updateTeam(TeamUpdateRequest teamUpdateRequest, User loginUser) {
        if (teamUpdateRequest == null) {
            throw new NotFoundException(20008);
        }
        Long id = teamUpdateRequest.getId();
        if(id == null || id <= 0){
            throw new NotFoundException(20008);
        }
        Team oldTeam = this.getById(id);
        if(oldTeam == null){
            throw new NotFoundException(20008);
        }
        //todo userService.isAdmin重写
        if (oldTeam.getUserId() != loginUser.getId() && !UserUtils.isAdmin(loginUser)) {
            throw new NotFoundException(20008);
        }
        TeamStatusEnum statusEnum =
                TeamStatusEnum.getEnum(teamUpdateRequest.getStatus());
        if (TeamStatusEnum.SECRET.equals(statusEnum)){
            if (StringUtils.isBlank(teamUpdateRequest.getPassword())){
                throw new NotFoundException(20008);
            }
        }
        Team updateTeam = new Team();
        //拷贝修改的队伍信息
        //todo BeanUtils.copyProperties可能有问题
        BeanUtils.copyProperties(teamUpdateRequest,updateTeam);
        return this.updateById(updateTeam);
    }

    /**
     * 加入队伍
     * @param teamJoinRequest
     * @param loginUser
     * @return
     */
    @Override
    public boolean joinTeam(TeamJoinRequest teamJoinRequest, User loginUser) {
        if (teamJoinRequest == null) {
            throw new NotFoundException(20008);
        }
        //队伍必须存在，只能加入未满、未过期的队伍
        Long teamId = teamJoinRequest.getTeamId();
        if (teamId == null || teamId <= 0) {
            throw new NotFoundException(20008);
        }
        Team team = this.getById(teamId);
        if (team == null) {
            throw new NotFoundException(20008);
        }
        Date expireTime = team.getExpireTime();
        if (expireTime != null && expireTime.before(new Date())) {
            throw new NotFoundException(20008);
        }
        //禁止加入私有队伍
        Integer status = team.getStatus();
        TeamStatusEnum statusEnum = TeamStatusEnum.getEnum(status);
        if (TeamStatusEnum.PRIVATE.equals(statusEnum)) {
            throw new NotFoundException(20008);
        }
        //如果加入的队伍是加密的，必须密码要匹配才可以
        String password = teamJoinRequest.getPassword();
        if (TeamStatusEnum.SECRET.equals(statusEnum)) {
            if (StringUtils.isBlank(password) || !password.equals(team.getPassword())) {
                throw new NotFoundException(20008);
            }
        }
        //用户最多加入5个队伍
        long userId = loginUser.getId();
        QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
        userTeamQueryWrapper.eq("userId", userId);
        long hasJoinTeam = userTeamService.count(userTeamQueryWrapper);
        if (hasJoinTeam > 5) {
            throw new NotFoundException(20008);
        }
        //用户不能重复加入已加入的队伍
        userTeamQueryWrapper = new QueryWrapper<>();
        userTeamQueryWrapper.eq("userId", userId);
        userTeamQueryWrapper.eq("teamId", teamId);
        long hasUserJoinTeam = userTeamService.count(userTeamQueryWrapper);
        if (hasUserJoinTeam > 0) {
            throw new NotFoundException(20008);
        }
        //用户加入的队伍必须是未满的
        userTeamQueryWrapper = new QueryWrapper<>();
        userTeamQueryWrapper.eq("teamId", teamId);
        long teamHasJoinNum = userTeamService.count(userTeamQueryWrapper);
        if (teamHasJoinNum >= team.getMaxNum()) {
            throw new NotFoundException(20008);
        }
        //修改队伍信息
        UserTeam userTeam = new UserTeam();
        userTeam.setUserId(userId);
        userTeam.setTeamId(teamId);
        userTeam.setJoinTime(new Date());
        return userTeamService.save(userTeam);
    }

    /**
     * 推出队伍
     * @param teamQuiteRequest
     * @param loginUser
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean quieTeam(TeamQuiteRequest teamQuiteRequest, User loginUser){
        if (teamQuiteRequest == null) {
            throw new NotFoundException(20008);
        }
        Long teamId = teamQuiteRequest.getTeamId();
        if (teamId == null || teamId <= 0) {
            throw new NotFoundException(20008);
        }
        Team team = this.getById(teamId);
        if (team == null) {
            throw new NotFoundException(20008);
        }

        Long userId = loginUser.getId();
        UserTeam quertUserTeam = new UserTeam();
        quertUserTeam.setUserId(userId);
        quertUserTeam.setTeamId(teamId);
        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>(quertUserTeam);
        long count = userTeamService.count(queryWrapper);
        if (count == 0) {
            throw new NotFoundException(20008);
        }
        long teamHasJoinNum = countTeamUser(teamId);
        if (teamHasJoinNum == 1) {
            this.removeById(teamId);
        }else {
            if (userId.equals(team.getUserId())) {
                QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
                userTeamQueryWrapper.eq("teamId", teamId);
                userTeamQueryWrapper.last("order by id asc limit 2");
                List<UserTeam> userTeamList = userTeamService.list(userTeamQueryWrapper);
                if (CollectionUtils.isEmpty(userTeamList) || userTeamList.size() <= 1) {
                    throw new NotFoundException(20008);
                }
                UserTeam nextUserTeam = userTeamList.get(1);
                Long nextUserId = nextUserTeam.getUserId();
                Team updateTeam = new Team();
                updateTeam.setId(teamId);
                updateTeam.setUserId(nextUserId);
                boolean result = this.updateById(updateTeam);
                if (!result) {
                    throw new NotFoundException(20008);
                }
            }
        }
        return userTeamService.remove(queryWrapper);
    }

    /**
     * 删除队伍
     * @param id
     * @param loginUser
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean deleteTeam(Long id, User loginUser) {
        Team team = getTeamById(id);
        long teamId = team.getId();
        if (! loginUser.getId().equals(team.getUserId())) {
            throw new NotFoundException(20008);
        }
        QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
        userTeamQueryWrapper.eq("teamId", teamId);
        //删除队伍关系
        boolean result = userTeamService.remove(userTeamQueryWrapper);
        if (!result) {
            throw new NotFoundException(20008);
        }
        //删除队伍
        return this.removeById(id);
    }









    /**
     * 根据队伍id获取队伍
     * @param teamId
     * @return
     */
    private Team getTeamById(Long teamId) {
        if (teamId == null || teamId <= 0){
            throw new NotFoundException(20008);
        }

        Team team = this.getById(teamId);
        if (team == null){
            throw new NotFoundException(20008);
        }
        return team;
    }



    /**
     * 获取队伍当前的人数
     * @param teamId
     * @return
     */
    private long countTeamUser(long teamId){
        QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
        userTeamQueryWrapper.eq("teamId",teamId);
        return userTeamService.count(userTeamQueryWrapper);
    }

    private Team createTeam(TeamAddRequest team) {
        Team newteam = new Team();
        newteam.setIsDelete(0);
        newteam.setName(team.getName());
        newteam.setDescription(team.getDescription());
        newteam.setMaxNum(team.getMaxNum());
        newteam.setExpireTime(team.getExpireTime());
        newteam.setUserId(team.getUserId());
        newteam.setStatus(team.getStatus());
        newteam.setPassword(team.getPassword());
        newteam.setCreateTime(new Date());
        newteam.setUpdateTime(new Date());
        return newteam;
    }
}





