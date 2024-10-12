package com.guidian.searchFriends.utils;

import com.guidian.searchFriends.model.User;

public class UserUtils {

    public static boolean isAdmin(User loginUser) {
        if (loginUser == null) {
            return false;
        }
        if(loginUser.getUserRole() == null) {
            return false;
        }
        return loginUser.getUserRole() == 1;
    }

}
