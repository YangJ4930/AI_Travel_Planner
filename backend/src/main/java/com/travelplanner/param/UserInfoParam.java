package com.travelplanner.param;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserInfoParam implements Serializable {

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 邮箱
     */
    private String email;
}
