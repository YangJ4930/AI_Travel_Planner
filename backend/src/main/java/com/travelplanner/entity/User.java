package com.travelplanner.entity;

import com.baomidou.mybatisplus.annotation.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@TableName("user")
public class User {
    
    @TableId(type = IdType.AUTO)
    private Long id;


    /**
     * 用户名称
     */
    private String username;


    /**
     * 邮箱
     */
    private String email;


    /**
     *  密码
     */
    private String password;


    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 是否删除（逻辑删除）
     */
    @TableLogic
    private Boolean isDelete;
}