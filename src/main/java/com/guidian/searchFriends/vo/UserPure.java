/**
 * @作者 徐振博
 * @创建时间 2024/6/12 15:53
 */
package com.guidian.searchFriends.vo;

import com.guidian.searchFriends.model.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserPure {
    private String username;
    private String userAccount;
    private String avatarUrl;
    private Integer gender;
    private String phone;
    private String email;
    private Integer userStatus;
    private Integer userRole;
    private String planetCode;
    private String tags;

    public UserPure(User user) {
        this.username = user.getUserName();
        this.userAccount = user.getUserAccount();
        this.avatarUrl = user.getAvatarUrl();
        this.gender = user.getGender();
        this.phone = user.getPhone();
        this.email = user.getEmail();
        this.userStatus = user.getUserStatus();
        this.userRole = user.getUserRole();
        this.planetCode = user.getPlanetCode();
        this.tags = user.getTags();

    }
}
