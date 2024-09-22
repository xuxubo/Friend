/**
 * @作者 徐振博
 * @创建时间 2024/2/28 15:57
 */
package com.guidian.searchFriends.exception;


import com.guidian.searchFriends.exception.http.HttpException;

public class LogOutSuccess extends HttpException {

    public LogOutSuccess(int code) {

        this.httpStatusCode = 200;
        this.code = code;
    }
}
