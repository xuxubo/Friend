/**
 * @作者 徐振博
 * @创建时间 2024/1/18 16:19
 */
package com.guidian.searchFriends.exception.http;

public class NotFoundException extends HttpException {
    public NotFoundException(Integer code) {
        this.code = code;
        this.httpStatusCode = 404;
    }
}
