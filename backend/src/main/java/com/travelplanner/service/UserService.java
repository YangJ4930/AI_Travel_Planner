package com.travelplanner.service;

import com.travelplanner.entity.User;
import com.travelplanner.param.UserInfoParam;
import com.travelplanner.vo.user.UserInfoVO;
import com.travelplanner.vo.user.UserLoginVO;

public interface UserService {

    UserInfoVO getUserById(Long id);

    UserLoginVO register(UserInfoParam param);

    UserLoginVO login(UserInfoParam param);

}
