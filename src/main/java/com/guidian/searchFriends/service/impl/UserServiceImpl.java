package com.guidian.searchFriends.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.guidian.searchFriends.model.dto.UserLoginDTO;
import com.guidian.searchFriends.model.dto.UserRegisterDTO;
import com.guidian.searchFriends.exception.http.NotFoundException;
import com.guidian.searchFriends.exception.http.ServerErrorException;
import com.guidian.searchFriends.exception.http.UnAuthenticatedException;
import com.guidian.searchFriends.model.User;
import com.guidian.searchFriends.service.UserService;
import com.guidian.searchFriends.mapper.UserMapper;
import com.guidian.searchFriends.vo.UserPure;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.internal.constraintvalidators.bv.time.futureorpresent.FutureOrPresentValidatorForCalendar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author 27605
 * @description 针对表【user(用户)】的数据库操作Service实现
 * @createDate 2024-05-13 22:04:26
 */
@Service
@Slf4j
@Transactional
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {


    @Autowired
    private UserMapper userMapper;

    private final String SALT = "Xuzhenbo";

    /**
     * 用户注册
     *
     * @param user
     * @author xu
     */
    @Override
    public void userRegister(UserRegisterDTO user) {
        if (!user.getUserPassword1().equals(user.getUserPassword2())) {
            throw new ServerErrorException(20000);
        }
        // 1.4. 账户不包含特殊字符
        String validRule = "[`~!@#$%^&*()+=|{}':;',.<>/?~！@#￥%……&*（）——+ | {}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(validRule).matcher(user.getUserAccount());
        // 如果包含非法字符，则返回
        if (matcher.find()) {
            throw new ServerErrorException(20000);
        }


        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", user.getUserAccount());
        /**
         * 不允许重复账号
         */
        Long count = userMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new ServerErrorException(20001);
        }

        String verifyPassword = DigestUtils.md5DigestAsHex((SALT +
                user.getUserPassword1()).getBytes(StandardCharsets.UTF_8));
        User userSave = new User();
        userSave.setUserAccount(user.getUserAccount());
        userSave.setUserPassword(verifyPassword);
        int res = userMapper.insert(userSave);
        if (res < 0) {
            throw new ServerErrorException(20000);
        }
    }

    /**
     * 用户登陆
     * @param user
     * @param request
     * @return
     */
    @Override
    public User userLogin(UserLoginDTO user, HttpServletRequest request) {


        String encodePassword = DigestUtils.md5DigestAsHex((SALT +
                user.getUserPassword()).getBytes(StandardCharsets.UTF_8));
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        // 这里存在bug：会把逻辑删除的用户查出来
        queryWrapper.eq("userAccount", user.getUserAccount());
        queryWrapper.eq("userPassword", encodePassword);
        User selectUser = userMapper.selectOne(queryWrapper);
        if (selectUser == null) {
            log.info("user login failed, userAccount Cannot match userPassword");
            throw new UnAuthenticatedException(20003);
        }

        User newUser = new User();
        newUser.setId(selectUser.getId());
        newUser.setUserName(selectUser.getUserName());
        newUser.setUserAccount(selectUser.getUserAccount());
        newUser.setAvatarUrl(selectUser.getAvatarUrl());
        newUser.setGender(selectUser.getGender());
        newUser.setPhone(selectUser.getPhone());
        newUser.setEmail(selectUser.getEmail());
        newUser.setUserStatus(selectUser.getUserStatus());
        newUser.setCreateTime(selectUser.getCreateTime());

        // 4.记录用户的登录态（session），将其存到服务器上
        request.getSession().setAttribute("user", newUser);
        // 5.返回脱敏后的用户信息
        return newUser;
    }



    /**
     * 根据标签查找用户
     * @param tagNameList
     * @return
     */
    @Override
    public List<UserPure> searchUserByTags(List<String> tagNameList) {
        if (CollectionUtils.isEmpty(tagNameList)) {
            throw new NotFoundException(20002);
        }
//        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
//        for (String tagList : tagNameList) {
//            queryWrapper = queryWrapper.like("tags", tagList);
//        }
//        List<User> users = userMapper.selectList(queryWrapper);
//        return users.stream().map(user -> new UserPure(user)).collect(Collectors.toList());
//
        QueryWrapper queryWrapper = new QueryWrapper();
        List<User> users = userMapper.selectList(queryWrapper);
        //Google序列化组件
        Gson gson = new Gson();
        //流操作
        return users.stream().filter(user -> {
            String tagstr = user.getTags();
            /**
             * 检查是否为空白字符
             */
            if(StringUtils.isBlank(tagstr)){
                return false;
            }
            Set<String> tempTagNameSet = gson.fromJson(tagstr, new TypeToken<Set<String>>() {
            }.getType());
            for (String tagName : tagNameList) {
                if (!tempTagNameSet.contains(tagName)) {
                    return false;
                }
            }
            return true;

        }).map(UserPure::new).collect(Collectors.toList());

      }

    /**
     * 获取当前登陆的用户
     * @param request
     * @return
     */
    @Override
    public User getLoginUser(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        Object userObj = request.getSession().getAttribute("user");
        return null;
    }

    /**
     * 更新用户信息
     * @param user
     * @param loginUser
     * @return
     */
    @Override
    public Integer updateUser(User user, User loginUser) {
        return null;
    }


    /**
     * 根据Id获取用户信息
     * @param id
     * @return
     */
    @Override
    public User selectUser(Long id) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("id", id);
        User user = userMapper.selectOne(queryWrapper);
        if (user == null){
            throw new NotFoundException(20002);
        }
        return user;
    }

    /**
     * 返回所有用户
     * @return
     */
    public List<User> selectListUser() {
        QueryWrapper queryWrapper = new QueryWrapper();
        List<User> users = userMapper.selectList(queryWrapper);
        return users;
    }

    /**
     * 返回某页的所有用户
     *
     * @return
     */
    public Page<User> selectUserPage(int pageNum, int count) {
        Page<User> page = new Page<>(pageNum, count);
        QueryWrapper<User> queryWrapper = new QueryWrapper();

        Page<User> users = userMapper.selectPage(page, queryWrapper);
        return users;
    }


}





