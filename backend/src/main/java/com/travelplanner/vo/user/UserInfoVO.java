package com.travelplanner.vo.user;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserInfoVO implements Serializable {

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;
}
