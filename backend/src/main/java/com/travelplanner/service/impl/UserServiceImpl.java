package com.travelplanner.service.impl;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import com.travelplanner.common.enums.ResponseCodeEnum;
import com.travelplanner.common.enums.TravelPlannerException;
import com.travelplanner.entity.User;
import com.travelplanner.param.UserInfoParam;
import com.travelplanner.service.UserService;
import com.travelplanner.service.base.UserBaseService;
import com.travelplanner.util.ParamCheckUtil;
import com.travelplanner.vo.user.UserInfoVO;
import com.travelplanner.vo.user.UserLoginVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Resource
    UserBaseService userBaseService;

    @Override
    public UserLoginVO login(UserInfoParam param) {
        try {
            validateUserInfo(param.getUsername(), param.getPassword());
            User user = userBaseService.getByUsername(param.getUsername());
            if (user == null || !param.getPassword().equals(user.getPassword())) {
                throw new TravelPlannerException(ResponseCodeEnum.PARAM_ERROR, "用户名或密码错误");
            }
            return doSaTokenLogin(user.getId());
        } catch (TravelPlannerException e) {
            log.error("UserServiceImpl#login error 登录失败, param: {}", param, e);
            throw e;
        } catch (Exception e) {
            log.error("UserServiceImpl#login error 登录失败, param: {}", param, e);
            throw new TravelPlannerException(ResponseCodeEnum.SYSTEM_ERROR, "登录失败");
        }
    }

    @Override
    public UserInfoVO getUserById(Long id) {
        try {
            User user = userBaseService.getById(id);
            if (user == null) {
                throw new TravelPlannerException(ResponseCodeEnum.PARAM_ERROR, "用户不存在");
            }
            UserInfoVO userInfoVO = new UserInfoVO();
            userInfoVO.setUserId(user.getId());
            userInfoVO.setUsername(user.getUsername());
            return userInfoVO;
        } catch (TravelPlannerException e) {
            log.error("UserServiceImpl#getUserById error 获得用户信息失败, param: {}", id, e);
            throw e;
        } catch (Exception e) {
            log.error("UserServiceImpl#getUserById error 获得用户信息失败, param: {}", id, e);
            throw new TravelPlannerException(ResponseCodeEnum.SYSTEM_ERROR, "获得用户信息失败");
        }
    }

    @Override
    public UserLoginVO register(UserInfoParam param) {
        try {
            validateRegister(param.getUsername(), param.getPassword());
            User user = userBaseService.register(param.getUsername(), param.getPassword(), param.getEmail());
            return doSaTokenLogin(user.getId());
        } catch (TravelPlannerException e) {
            log.error("UserServiceImpl#register error 注册失败, param: {}", param, e);
            throw e;
        } catch (Exception e) {
            log.error("UserServiceImpl#register error 注册失败, param: {}", param, e);
            throw new TravelPlannerException(ResponseCodeEnum.SYSTEM_ERROR, "注册失败");
        }
    }

    private UserLoginVO doSaTokenLogin(Long userId) {
        StpUtil.login(userId);
        SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
        return UserLoginVO.builder()
                .tokenName(tokenInfo.tokenName)
                .tokenContent(tokenInfo.tokenValue)
                .build();
    }

    private void validateRegister(String userName, String password) {
        validateUserInfo(userName, password);
        User user = userBaseService.getByUsername(userName);
        if(Objects.nonNull(user)){
            throw new TravelPlannerException(ResponseCodeEnum.SYSTEM_ERROR, "用户名已被占用");
        }
    }

    private void validateUserInfo(String userName, String password) {
        ParamCheckUtil.notBlank(userName, "用户名不能为空");
        ParamCheckUtil.notBlank(password, "密码不能为空");
    }
}
