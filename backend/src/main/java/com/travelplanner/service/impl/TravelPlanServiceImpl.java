package com.travelplanner.service.impl;

import com.alibaba.fastjson2.JSON;
import com.travelplanner.common.enums.ResponseCodeEnum;
import com.travelplanner.common.enums.TravelPlannerException;
import com.travelplanner.dto.TravelPlanDto;
import com.travelplanner.param.TravelQueryParam;
import com.travelplanner.service.TravelPlanService;
import com.travelplanner.service.base.TravelPlanBaseService;
import com.travelplanner.wrapper.LLMTravelPlanWrapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TravelPlanServiceImpl implements TravelPlanService {

    @Resource
    LLMTravelPlanWrapper llmTravelPlanWrapper;

    @Resource
    TravelPlanBaseService travelPlanBaseService;

    @Override
    public void addTravelPlan(TravelQueryParam travelQueryParam, Long userId) {
        try {
            log.info("开始添加旅行计划, 查询参数: {}, 用户ID: {}", travelQueryParam, userId);
            
            // 调用LLM生成旅行计划
            TravelPlanDto travelPlanDto = llmTravelPlanWrapper.llmPlan(travelQueryParam.getQuery());
            
            // 将content列表序列化为JSON字符串
            String contentJson = JSON.toJSONString(travelPlanDto.getContent());
            log.info("旅行计划内容序列化完成, 标题: {}", travelPlanDto.getTitle());
            // 调用基础服务保存旅行计划
            travelPlanBaseService.addTravelPlan(
                travelQueryParam.getQuery(),  // 原始查询
                travelPlanDto.getTitle(),     // 计划标题
                contentJson,                  // 序列化后的内容
                userId                        // 用户ID
            );
            
            log.info("旅行计划添加成功");
        } catch (TravelPlannerException travelPlannerException) {
            log.error("TravelPlanServiceImpl#addTravelPlan error 添加旅游计划失败, param: {}", travelQueryParam, travelPlannerException);
            throw travelPlannerException;
        } catch (Exception exception) {
            log.error("TravelPlanServiceImpl#addTravelPlan error 添加旅游计划失败, param: {}", travelQueryParam, exception);
            throw new TravelPlannerException(ResponseCodeEnum.SYSTEM_ERROR, "添加旅游计划失败");
        }
    }
}
