package com.travelplanner.service.base.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.travelplanner.entity.TravelPlan;
import com.travelplanner.mapper.TravelPlanMapper;
import com.travelplanner.service.base.TravelPlanBaseService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TravelPlanBaseServiceImpl  extends ServiceImpl<TravelPlanMapper, TravelPlan> implements TravelPlanBaseService {

    @Override
    public void addTravelPlan(String query, String title, String content, Long userId) {
        TravelPlan travelPlan = new TravelPlan();
        travelPlan.setTitle(title);
        travelPlan.setQuery(query);
        travelPlan.setContent(content);
        travelPlan.setUserId(userId);
        this.save(travelPlan);
    }

    @Override
    public List<TravelPlan> list(Long userId) {
        LambdaQueryWrapper<TravelPlan> listLambdaQueryWrapper = new LambdaQueryWrapper<TravelPlan>().eq(TravelPlan::getUserId, userId);
        return this.list(listLambdaQueryWrapper);
    }
}
