package com.travelplanner.service;

import com.travelplanner.entity.TravelPlan;
import com.travelplanner.param.TravelQueryParam;
import com.travelplanner.vo.travelPlanner.TravelPlanDetailVo;
import com.travelplanner.vo.travelPlanner.TravelPlanVo;

import java.util.List;

public interface TravelPlanService {

    void addTravelPlan(TravelQueryParam travelQueryParam, Long userId);

    List<TravelPlanVo> listTravelPlan(Long userId);

    TravelPlanDetailVo getTravelPlanDetail(Long id);

}
