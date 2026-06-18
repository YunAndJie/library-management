//TODO在这里存放响应码



package com.pwenjie.common.result;

public interface ResultCode {
    int SUCCESS = 200;

    //客户端错误
    int BAD_REQUEST = 400;     //请求参数错误
    int UNAUTHORIZED = 401;    //未授权
    int FORBIDDEN = 403;       //禁止访问
    int NOT_FOUND = 404;       //资源不存在

    //服务器错误
    int INTERNAL_SERVER_ERROR = 500;  //服务器内部错误
}
