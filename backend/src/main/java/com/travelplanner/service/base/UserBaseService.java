package com.travelplanner.service.base;

import com.baomidou.mybatisplus.extension.service.IService;
import com.travelplanner.entity.User;

public interface UserBaseService extends IService<User> {

    User getByUsername(String username);

    User register(String username, String password, String email);
}
