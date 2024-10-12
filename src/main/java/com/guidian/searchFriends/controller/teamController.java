/**
 * @作者 徐振博
 * @创建时间 2024/9/5 16:49
 */
package com.guidian.searchFriends.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.guidian.searchFriends.core.UnifyResponse;
import com.guidian.searchFriends.exception.http.NotFoundException;
import com.guidian.searchFriends.model.Team;
import com.guidian.searchFriends.model.User;
import com.guidian.searchFriends.model.dto.TeamQuery;
import com.guidian.searchFriends.model.request.TeamAddRequest;
import com.guidian.searchFriends.model.request.TeamJoinRequest;
import com.guidian.searchFriends.model.request.TeamQuiteRequest;
import com.guidian.searchFriends.model.request.TeamUpdateRequest;
import com.guidian.searchFriends.service.TeamService;
import com.guidian.searchFriends.utils.UserUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Objects;

@CrossOrigin(origins = {"http://localhost:3000", "null"},allowCredentials = "true")
@RestController
@RequestMapping("/team")
@Transactional
public class teamController {

    @Autowired
    private TeamService teamService;
//    查找自己创建的队伍 get
//    http://localhost:8080/api/team/list/my/create?searchText=&pageNum=1

    /**
     * 我创建的队伍
     * @param request
     * @return
     */
    @GetMapping("/list/my/create")
    public UnifyResponse<List<Team>> myTeam(HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute("user");
        List<Team> teams = teamService.getByUserId(user.getId());
        return new UnifyResponse(200, "成功", teams);
    }


//    查找自己加入的队伍 GET
//    http://localhost:8080/api/team/list/my/join?searchText=&pageNum=1

    /**
     * 我加入的队伍
     * @param request
     * @return
     */
    @GetMapping("/list/my/join")
    public UnifyResponse<List<Team>> myJoinTeam(HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute("user");
        List<Team> teams = teamService.getJoinTeam(user.getId());
        return new UnifyResponse<>(200, "成功", teams);

    }

//    查找队伍 GET
//    http://localhost:8080/api/team/list?searchText=&pageNum=1&status=0

    /**
     * 查找队伍
     * @param request
     * @param status 队伍状态
     * @param pageNum 第几页
     * @param searchText 文本描述
     * @return
     */
    @GetMapping("/list")
    public UnifyResponse<List<Team>> teams(HttpServletRequest request,@RequestParam(defaultValue = "8") Integer status,@RequestParam(defaultValue = "1")Integer pageNum,@RequestParam(defaultValue = "") String searchText) {
        List<Team> teamList;
        if (Objects.equals(searchText, "")) {
            teamList = teamService.getBaseMapper().selectList(new QueryWrapper<>());
            return new UnifyResponse<>(200, "成功", teamList);
        }
        teamList = teamService.listTeams(searchText,status,pageNum, UserUtils.isAdmin((User) request.getSession().getAttribute("user")));
        return new UnifyResponse<>(200, "成功", teamList);
    }

//    创建队伍 GET
//    http://localhost:8080/api/team/add

    @PostMapping("/add")
    public UnifyResponse addTeam(HttpServletRequest request, @RequestBody TeamAddRequest team) {
        User loginUser = (User) request.getSession().getAttribute("user");

        long isInsert = teamService.addTeam(team, loginUser);
        if (isInsert <= 0){
            throw new NotFoundException(20008);
        }
        return new UnifyResponse(200, "插入成功");
    }


    /**
     * 根据ID查询队伍
     * @param request
     * @param id
     * @return
     */
    @GetMapping("/get")
    public UnifyResponse<Team> getTeamById(HttpServletRequest request, @RequestParam(name = "id" ) Long id) {
        User loginUser = (User) request.getSession().getAttribute("user");
        Team team = teamService.getBaseMapper().selectById(id);
        if(Objects.equals(team.getUserId(), loginUser.getId())){
            return new UnifyResponse<>(200, "成功", team);
        }
        throw new NotFoundException(20008);
    }

    /**
     * 更新队伍
     * @param request
     * @param team
     * @return
     */
    @PostMapping("/update")
    public UnifyResponse updateTeam(HttpServletRequest request,@RequestBody TeamUpdateRequest team) {
        User loginUser = (User) request.getSession().getAttribute("user");
        if (!teamService.updateTeam(team, loginUser)) {
            throw new NotFoundException(20008);
        }
        return new UnifyResponse(200, "更新成功");
    }

    /**
     * 删除队伍
     * @param request
     * @param team
     * @return
     */
    @PostMapping("/delete")
    public UnifyResponse deleteTeam(HttpServletRequest request, @RequestBody Team team) {
        User loginUser = (User) request.getSession().getAttribute("user");
        if (!teamService.deleteTeam(team.getId(), loginUser)) {
            throw new NotFoundException(20008);
        }
        return new UnifyResponse<>(200, "删除成功");
    }

    //加入队伍
    @PostMapping("/join")
    public UnifyResponse joinTeam(HttpServletRequest request, @RequestBody TeamJoinRequest team) {
        User loginUser = (User) request.getSession().getAttribute("user");
        if (!teamService.joinTeam(team, loginUser)) {
            throw new NotFoundException(20008);
        }
        return new UnifyResponse<>(200, "加入成功");
    }

    //推出队伍
    @PostMapping("/quite")
    public UnifyResponse quiteTeam(HttpServletRequest request, @RequestBody TeamQuiteRequest team) {
        User loginUser = (User) request.getSession().getAttribute("user");
        if (!teamService.quieTeam(team, loginUser)) {
            throw new NotFoundException(20008);
        }
        return new UnifyResponse<>(200, "退出成功");
    }
}

