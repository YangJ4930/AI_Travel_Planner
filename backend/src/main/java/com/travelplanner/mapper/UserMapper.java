package com.travelplanner.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.travelplanner.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
