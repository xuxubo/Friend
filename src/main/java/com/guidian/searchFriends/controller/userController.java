/**
 * @作者 徐振博
 * @创建时间 2024/5/5 8:21
 */
package com.guidian.searchFriends.controller;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.guidian.searchFriends.core.UnifyResponse;
import com.guidian.searchFriends.dto.UserLoginDTO;
import com.guidian.searchFriends.exception.UserLogin;
import com.guidian.searchFriends.exception.http.NotFoundException;
import com.guidian.searchFriends.model.User;
import com.guidian.searchFriends.service.UserService;
import com.guidian.searchFriends.vo.UserPure;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

//跨域配置
@CrossOrigin(origins = {"http://localhost:3000", "null"},allowCredentials = "true")
@RestController
@RequestMapping("/user")
@Slf4j
public class userController {

    @Autowired
    private UserService userService;

    @Resource
    private RedisTemplate redisTemplate;

    /**
     * 根据标签查找用户
     * @param tagNameList
     * @return
     */
    @GetMapping("/search/tags")
    public UnifyResponse<List<UserPure>> searchUsers(@RequestParam(required = false) List<String> tagNameList) {
        if (CollectionUtils.isEmpty(tagNameList)) {
            throw new NotFoundException(20002);
        }
        List<UserPure> userPureList = userService.searchUserByTags(tagNameList);
        return new UnifyResponse<>(200, "成功", userPureList);
    }


    /**
     * 更新用户信息
     *
     * @param newUser
     * @param request
     * @return
     */
    @PostMapping("/update")
    public UnifyResponse<Integer> updateUser(@RequestBody User newUser, HttpServletRequest request) {
        isLogin(request);
        User loginUser = userService.getLoginUser(request);

        if (loginUser == null) {
            throw new NotFoundException(20007);
        }
        return new UnifyResponse<Integer>(200, "成功", 1);

    }

    /**
     * 直接分页查询用户
     * @param request
     * @return
     */
    @GetMapping("recommend")
    public UnifyResponse<List<User>> recommendUser(@RequestParam(defaultValue = "8") Integer pageSize, @RequestParam(defaultValue = "1") Integer pageNum, HttpServletRequest request) {
        User loginUser = isLogin(request);
        String redisKey = String.format("yupao:user:recommend:%s", loginUser.getId());
        //读Redis
        ValueOperations valueOperations = redisTemplate.opsForValue();
        List<User> userPage = (List<User>) valueOperations.get(redisKey);
        //如果Redis不为则直接返回数据
        if (userPage != null) {
            return new UnifyResponse<>(200, "成功", userPage);
        }
        Page<User> pageUser = userService.selectUserPage(pageNum, pageSize);
        //写入Redis(注意设置过期时间)
        List<User> userList = pageUser.getRecords();

        try {
            valueOperations.set(redisKey, userList, 1, TimeUnit.HOURS);
        } catch (Exception e) {
            log.error("redis set key error", e);
        }
        return new UnifyResponse<>(200, "成功", userList);
        
    }

//    @GetMapping("/logOut")
//    public UnifyResponse<Integer> LogOut(HttpServletRequest request){
//        if (request == null) {
//            throw new ServerErrorException(9999);
//        }
//        Integer id =  userService.userLogOut(request);
//        if (id != 1) {
//            throw new NotFoundException(20002);
//        }
//        return new UnifyResponse(200, "注销成功");
//    }
//
//    /**
//     * 用户注册返回
//     * @param user
//     * @return 用户Id
//     */
//    @PostMapping("/register")
//    public UnifyResponse<String> userRegister(@Validated @RequestBody UserRegisterDTO user) {
//        if(!user.getPassword1().equals(user.getPassword2())){
//            throw new ServerErrorException(20000);
//        }
//        userService.userRegister(user);
//        return new UnifyResponse<>(200, "ok", user.getUserAccount());
//    }
//
    /**
     * 用户登陆
     * @param user
     * @param request
     * @return 用户信息
     */
    @PostMapping("/login")
    public UnifyResponse<User> userLogin(@Validated @RequestBody UserLoginDTO user, HttpServletRequest request) {

        User loginUser =  userService.userLogin(user, request);
        System.out.println(loginUser);

        return new UnifyResponse<>(200, "ok", loginUser);
    }
//
//    /**
//     * 根据Id获取用户信息
//     * @param id
//     * @param request
//     * @return current User
//     */
//    @GetMapping("/select/{id}")
//    public UnifyResponse<User> selectUser(@PathVariable Long id, HttpServletRequest request) {
//
//        if (!isAdmin(request)) {
//            throw new ForbiddenException(10005);
//        }
//        User user = userService.selectUser(id);
//        return new UnifyResponse<>(200, user);
//
//    }
//    @GetMapping("/delete")
//    public UnifyResponse<Boolean> DeleteUser(@RequestBody Long id, HttpServletRequest request) {
//        if (!isAdmin(request)) {
//            throw new ForbiddenException(10005);
//        }
//
//        if (userService.deleteUser(id)) {
//            return new UnifyResponse<Boolean>(200,"删除成功", null);
//        }
//        throw new ServerErrorException(10007);
//    }
//
    /**
     * 当前用户信息
     *
     * @param request
     * @return
     */
    @GetMapping("current")
    public UnifyResponse<User> currentUser(HttpServletRequest request) {
//        Object user = request.getSession().getAttribute("user");
//        if (user == null) {
//            throw new NotFoundException(20006);
//        }
//        User currentUser = (User) user;
//
//        User selectUser = userService.selectUser(currentUser.getId());

        isLogin(request);
        User user = (User) request.getSession().getAttribute("user");
        User selectUser = userService.selectUser(user.getId());
        return new UnifyResponse<>(200, "成功", selectUser);
    }

//
//    @PostMapping("/updateUser")
//    public UnifyResponse<Long> updateUser(HttpServletRequest request, @RequestBody User user) {
//        if (!isAdmin(request)) {
//            throw new ForbiddenException(10005);
//        }
//        if (user == null) {
//            throw new ServerErrorException(20002);
//        }
//        Long id = userService.updateUser(user);
//        return new UnifyResponse<>(200, "ok");
//
//    }
//
//
//    /**
//     * 是否为管理员，是返回true 否者返回false
//     * @param request
//     * @return
//     */
//    private Boolean isAdmin(HttpServletRequest request) {
//        User user = (User) request.getSession().getAttribute("user");
//
//        User newUser =  userService.getById(user.getId());
//        if (newUser.getUserRole() == 0) {
//            return false;
//        }
//        return true;
//    }

    /**
     //     * 是否登陆
     //     * @param request
     //     * @return
     //     */
    private User isLogin(HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            throw new UserLogin(40100);
        }
        return user;
    }


}
