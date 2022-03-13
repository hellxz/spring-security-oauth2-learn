package com.github.hellxz.oauth2.web;

import com.github.hellxz.oauth2.dto.UserDto;
import com.github.hellxz.oauth2.feign.UserFeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 测试调用用户资源服务器的controller，用于测试
 */
@RestController
public class UserController {

    @Resource
    UserFeignClient userClient;

    @GetMapping("/user")
    public UserDto getResourceUser(){
        return userClient.getUser("test");
    }
}
