package com.travelplanner.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.travelplanner.common.Response;
import com.travelplanner.entity.TravelPlan;
import com.travelplanner.param.TravelQueryParam;
import com.travelplanner.service.TravelPlanService;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/travel-plan")
@RequiredArgsConstructor
public class TravelPlanController {

    @Resource
    TravelPlanService travelPlanService;

    @PostMapping("")
    public Response<Void> addTravelPlan(@RequestBody TravelQueryParam travelQueryParam){
        travelPlanService.addTravelPlan(travelQueryParam, StpUtil.getLoginIdAsLong());
        return Response.success();
    }

}
