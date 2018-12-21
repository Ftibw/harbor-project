package com.whxm.harbor.annotation;

import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ApiResponses(
        {
                @ApiResponse(code = 200, message = "Successful — 请求已完成"),
                @ApiResponse(code = 201, message = "请求已经被实现，而且有一个新的资源已经依据请求的需要而建立"),
                @ApiResponse(code = 400, message = "请求中有语法问题，或不能满足请求"),
                @ApiResponse(code = 401, message = "未授权客户机访问数据"),
                @ApiResponse(code = 403, message = "禁止访问"),
                @ApiResponse(code = 404, message = "服务器找不到给定的资源；文档不存在"),
                @ApiResponse(code = 500, message = "服务器不能完成请求")
        }
)
public @interface MyApiResponses {
}
