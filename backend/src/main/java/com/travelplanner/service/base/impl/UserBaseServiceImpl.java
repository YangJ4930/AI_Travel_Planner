package com.travelplanner.service.base.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.travelplanner.entity.User;
import com.travelplanner.mapper.UserMapper;
import com.travelplanner.service.base.UserBaseService;
import org.springframework.stereotype.Service;

@Service
public class UserBaseServiceImpl extends ServiceImpl<UserMapper, User> implements UserBaseService {
    @Override
    public User getByUsername(String username) {
        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<User>().eq(User::getUsername, username);
        return this.getOne(lambdaQueryWrapper);
    }

    @Override
    public User register(String username, String password, String email) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);
        this.save(user);
        return user;
    }


}
