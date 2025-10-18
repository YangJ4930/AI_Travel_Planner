package com.travelplanner.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.travelplanner.aspect.annotation.LoginCheck;
import com.travelplanner.common.Response;
import com.travelplanner.param.UserInfoParam;
import com.travelplanner.service.UserService;
import com.travelplanner.vo.user.UserInfoVO;
import com.travelplanner.vo.user.UserLoginVO;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    @Resource
    UserService userService;

    @LoginCheck(allowAnonymous = true)
    @PostMapping("/register")
    public Response<UserLoginVO> register(@RequestBody UserInfoParam userInfoParam) {
        return Response.success(userService.register(userInfoParam));
    }

    @LoginCheck(allowAnonymous = true)
    @PostMapping("/login")
    public Response<UserLoginVO> login(@RequestBody UserInfoParam userInfoParam) {
        return Response.success(userService.login(userInfoParam));
    }

    @GetMapping
    public Response<UserInfoVO> getUserInfo() {
        System.out.println(StpUtil.getLoginIdAsLong());
        return Response.success(userService.getUserById(StpUtil.getLoginIdAsLong()));
    }


    @PostMapping("/logout")
    public Response<Void> logout() {
        StpUtil.logout();
        return Response.success();
    }

}
