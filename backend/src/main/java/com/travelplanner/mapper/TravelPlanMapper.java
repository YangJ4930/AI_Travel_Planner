package com.travelplanner.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.travelplanner.entity.TravelPlan;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TravelPlanMapper extends BaseMapper<TravelPlan> {
}
