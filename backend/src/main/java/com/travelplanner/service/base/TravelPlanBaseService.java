package com.travelplanner.service.base;

import com.baomidou.mybatisplus.extension.service.IService;
import com.travelplanner.entity.TravelPlan;
import com.travelplanner.entity.User;

import java.util.List;

public interface TravelPlanBaseService extends IService<TravelPlan> {

    void addTravelPlan(String query, String title, String content, Long userId);

    List<TravelPlan> list(Long userId);
}
