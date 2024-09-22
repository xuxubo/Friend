/**
 * @作者 徐振博
 * @创建时间 2024/1/18 16:20
 */
package com.guidian.searchFriends.exception.http;

public class ParameterException extends HttpException {
    public ParameterException(Integer code) {
        this.code = code;
        this.httpStatusCode = 403;
    }
}
