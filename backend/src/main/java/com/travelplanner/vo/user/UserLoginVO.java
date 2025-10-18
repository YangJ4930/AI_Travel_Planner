package com.travelplanner.vo.user;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class UserLoginVO implements Serializable {
    /**
     * 令牌名称
     */
    private String tokenName;

    /**
     * 令牌内容，用于身份验证的 token 字符串
     */
    private String tokenContent;
}
