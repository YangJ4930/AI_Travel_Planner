package com.travelplanner.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.travelplanner.common.Response;
import com.travelplanner.entity.TravelPlan;
import com.travelplanner.param.TravelQueryParam;
import com.travelplanner.service.TravelPlanService;
import com.travelplanner.vo.travelPlanner.TravelPlanDetailVo;
import com.travelplanner.vo.travelPlanner.TravelPlanVo;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/list")
    public Response<List<TravelPlanVo>> listTravelPlan(){
        return Response.success(travelPlanService.listTravelPlan(StpUtil.getLoginIdAsLong()));
    }

    @GetMapping("/{id}")
    public Response<TravelPlanDetailVo> getTravelPlan(@PathVariable Long id){
        return Response.success(travelPlanService.getTravelPlanDetail(id));
    }

}
