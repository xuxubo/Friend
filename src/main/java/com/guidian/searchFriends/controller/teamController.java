/**
 * @作者 徐振博
 * @创建时间 2024/9/5 16:49
 */
package com.guidian.searchFriends.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.guidian.searchFriends.core.UnifyResponse;
import com.guidian.searchFriends.exception.http.NotFoundException;
import com.guidian.searchFriends.model.Team;
import com.guidian.searchFriends.model.User;
import com.guidian.searchFriends.service.TeamService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;


@CrossOrigin(origins = {"http://localhost:3000", "null"})
@RestController
@RequestMapping("/team")
public class teamController {

    @Autowired
    private TeamService teamService;
//    查找自己创建的队伍 get
//    http://localhost:8080/api/team/list/my/create?searchText=&pageNum=1

    @GetMapping("/list/my/create")
    public UnifyResponse<List<Team>> myTeam(HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute("user");
        List<Team> teams = teamService.getByUserId(user.getId());
        return new UnifyResponse(200, "成功", teams);
    }


//    查找自己加入的队伍 GET
//    http://localhost:8080/api/team/list/my/join?searchText=&pageNum=1

    @GetMapping("/list/my/join")
    public UnifyResponse<List<Team>> myJoinTeam(HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute("user");
        List<Team> teams = teamService.getJoinTeam(user.getId());
        return new UnifyResponse(200, "成功", teams);
    }

//    查找队伍 GET
//    http://localhost:8080/api/team/list?searchText=&pageNum=1&status=0
    @GetMapping("/list")
    public UnifyResponse<List<Team>> teams(HttpServletRequest request,@RequestParam(defaultValue = "8") Integer pageSize,@RequestParam(defaultValue = "1")Integer pageNum) {

        QueryWrapper queryWrapper = new QueryWrapper();
        Page<Team> page = new Page<>(pageSize, pageNum);
        List<Team> teamList = teamService.getBaseMapper().selectList(queryWrapper);
        return new UnifyResponse<>(200, "成功", teamList);
    }



//    创建队伍 GET
//    http://localhost:8080/api/team/add

    @PostMapping("/add")
    public UnifyResponse addTime(HttpServletRequest request,@RequestBody Team team) {
        User loginUser = (User) request.getSession().getAttribute("user");
        team.setUserId(loginUser.getId());
        int isInsert = teamService.getBaseMapper().insert(team);
        if (isInsert <= 0){
            throw new NotFoundException(20008);
        }
        return new UnifyResponse(200, "插入成功");
    }

}
