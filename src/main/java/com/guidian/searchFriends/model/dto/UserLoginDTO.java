/**
 * @作者 徐振博
 * @创建时间 2024/8/2 9:29
 */
package com.guidian.searchFriends.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserLoginDTO {

    private String userAccount;

    private String userPassword;

}
