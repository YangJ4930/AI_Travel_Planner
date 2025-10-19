package com.travelplanner.service.impl;

import com.alibaba.fastjson2.JSON;
import com.travelplanner.common.enums.ResponseCodeEnum;
import com.travelplanner.common.enums.TravelPlannerException;
import com.travelplanner.dto.TravelPlanContentDto;
import com.travelplanner.dto.TravelPlanDto;
import com.travelplanner.entity.TravelPlan;
import com.travelplanner.param.TravelQueryParam;
import com.travelplanner.service.TravelPlanService;
import com.travelplanner.service.base.TravelPlanBaseService;
import com.travelplanner.vo.travelPlanner.TravelPlanDetailVo;
import com.travelplanner.vo.travelPlanner.TravelPlanVo;
import com.travelplanner.wrapper.LLMTravelPlanWrapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

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

    @Override
    public List<TravelPlanVo> listTravelPlan(Long userId) {
        try {
            List<TravelPlan> travelPlanList = travelPlanBaseService.list(userId);
            return travelPlanList.stream()
                    .map(this::convertVo)
                    .toList();
        } catch (TravelPlannerException travelPlannerException) {
            log.error("TravelPlanServiceImpl#listTravelPlan error 展示旅游计划失败, param: {}", userId, travelPlannerException);
            throw travelPlannerException;
        } catch (Exception exception) {
            log.error("TravelPlanServiceImpl#listTravelPlan error 展示旅游计划失败, param: {}", userId, exception);
            throw new TravelPlannerException(ResponseCodeEnum.SYSTEM_ERROR, "展示旅游计划失败");
        }
    }

    @Override
    public TravelPlanDetailVo getTravelPlanDetail(Long id) {
        try {
            TravelPlan travelPlan = travelPlanBaseService.getById(id);
            if (travelPlan == null) {
                throw new TravelPlannerException(ResponseCodeEnum.PARAM_ERROR, "旅行计划不存在");
            }
        return convertDetailVo(travelPlan);
        } catch (TravelPlannerException travelPlannerException) {
            log.error("TravelPlanServiceImpl#listTravelPlan error 展示旅游计划细节失败, param: {}", id, travelPlannerException);
            throw travelPlannerException;
        } catch (Exception exception) {
            log.error("TravelPlanServiceImpl#listTravelPlan error 展示旅游计划细节失败, param: {}", id, exception);
            throw new TravelPlannerException(ResponseCodeEnum.SYSTEM_ERROR, "展示旅游计划细节失败");
        }
    }

    private TravelPlanVo convertVo(TravelPlan travelPlan){
        TravelPlanVo travelPlanVo = new TravelPlanVo();
        travelPlanVo.setId(travelPlan.getId());
        travelPlanVo.setTitle(travelPlan.getTitle());
        travelPlanVo.setQuery(travelPlan.getQuery());

        return travelPlanVo;
    }

    private TravelPlanDetailVo convertDetailVo(TravelPlan travelPlan){
        TravelPlanDetailVo travelPlanDetailVo = new TravelPlanDetailVo();
        travelPlanDetailVo.setId(travelPlan.getId());
        travelPlanDetailVo.setTitle(travelPlan.getTitle());
        travelPlanDetailVo.setQuery(travelPlan.getQuery());
        // 将content字符串解析为List<TravelPlanContentVo>对象
        List<TravelPlanContentDto> contentList = JSON.parseArray(travelPlan.getContent(), TravelPlanContentDto.class);
        travelPlanDetailVo.setContent(contentList);
        return travelPlanDetailVo;
    }



}
