package com.guidian.searchFriends.service;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.guidian.searchFriends.model.dto.UserLoginDTO;
import com.guidian.searchFriends.model.dto.UserRegisterDTO;
import com.guidian.searchFriends.model.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.guidian.searchFriends.vo.UserPure;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;


import java.util.List;

/**
* @author 27605
* @description 针对表【user(用户)】的数据库操作Service
* @createDate 2024-05-13 22:04:26
*/

public interface UserService extends IService<User> {


    void userRegister(UserRegisterDTO user);

    User userLogin(UserLoginDTO user, HttpServletRequest request);

    List<UserPure> searchUserByTags(List<String> list);

    User getLoginUser(HttpServletRequest request);

    Integer updateUser(User user, User loginUser);



    User selectUser(Long id);

    List<User> selectListUser();

    Page<User> selectUserPage(int pageNum, int count);


}
