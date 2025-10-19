package com.travelplanner.wrapper;

import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationParam;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.aigc.generation.models.QwenParam;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.ResponseFormat;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.travelplanner.common.enums.ResponseCodeEnum;
import com.travelplanner.common.enums.TravelPlannerException;
import com.travelplanner.dto.TravelPlanDto;
import com.travelplanner.property.LLMProperty;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class LLMTravelPlanWrapper {

    @Resource
    LLMProperty llmProperty;

    public TravelPlanDto llmPlan(String query) {
        try {
            log.info("开始调用LLM生成旅行计划，查询内容: {}", query);
            GenerationResult generationResult = callWithMessage(query);
            String content = generationResult.getOutput().getChoices().get(0).getMessage().getContent();
            log.info("LLM返回内容: {}", content);
            
            // 使用FastJSON解析返回的JSON字符串为TravelPlanDto对象
            TravelPlanDto travelPlan = JSON.parseObject(content, TravelPlanDto.class);
            
            log.info("成功解析旅行计划: {}", travelPlan.getTitle());
            return travelPlan;
            
        } catch (ApiException e) {
            log.error("调用LLM API失败: {}", e.getMessage(), e);
            throw new TravelPlannerException(ResponseCodeEnum.SYSTEM_ERROR, "调用LLM API失败");
        } catch (NoApiKeyException e) {
            log.error("LLM API Key未配置或无效: {}", e.getMessage(), e);
            throw new TravelPlannerException(ResponseCodeEnum.SYSTEM_ERROR, "LLM API Key未配置或无效");
        } catch (InputRequiredException e) {
            log.error("LLM输入参数不完整: {}", e.getMessage(), e);
            throw new TravelPlannerException(ResponseCodeEnum.SYSTEM_ERROR, "LLM输入参数不完整");
        } catch (Exception e) {
            log.error("解析LLM返回结果失败: {}", e.getMessage(), e);
            throw new TravelPlannerException(ResponseCodeEnum.SYSTEM_ERROR, "解析LLM返回结果失败");
        }
    }

    private GenerationResult callWithMessage(String query) throws ApiException, NoApiKeyException, InputRequiredException {
        Generation gen = new Generation();
        Message systemMsg = Message.builder()
                .role(Role.SYSTEM.getValue())
                .content("""
                         请从用户输入中定制相应的旅行的策略，并按照指定的JSON Schema格式输出：

                         【输出格式要求】
                         输出必须严格遵循以下JSON结构：
                         {
                          "title": "字符串类型，必需字段，旅游计划的名字",
                          "content":
                                 [
                                     {
                                         "city": "字符串类型，必需字段，城市的名字",
                                         "date": "data类型，必须字段，如'2025-10-1'这种格式返回"
                                         "keyword": "字符串类型，必需字段，如'故宫'",
                                         "reason": "字符串类型，必需字段，如'故宫是名胜古迹，拥有众多藏品'",
                                         "cost": "double类型，必须字段，如'1000.00'",
                                         "travel: '字符串类型，必须字段，如'火车''"
                                     },
                                    {
                                         "city": "字符串类型，必需字段，城市的名字",
                                         "date": "data类型，必须字段，如'2025-10-2'这种格式返回"
                                         "keyword": "字符串类型，必需字段，如'颐和园'",
                                         "reason": "字符串类型，必需字段，如'颐和园是著名公园'",
                                         "cost": "double类型，必须字段，如'1000.00'",
                                         "travel: '字符串类型，必须字段，如'地铁''"
                                     },
                                 ]
                         }
                         
                         【字段提取规则】
                         1. city: 定制旅行的城市
                         2. date: 定制旅行的日期
                         3. keyword: 定制旅行的地点
                         4. reason: 举出去那里旅行的原因
                         5. cost: 旅行的开销
                         6. travel: 给出交通工具，跨城可以给出飞机或者火车，同城可以用公交和高铁
                         7. title: 旅游计划的名字
                         
                         【参考示例】
                         示例1：(推荐两天行程)
                         Q：我想去带着孩子，从河北到北京玩两天有什么好推荐的？
                         A：
                         {  
                            "title": 两天北京游览计划，
                            "content":
                             [{
                                 "city": "北京",
                                 "date": "2025-10-1"
                                 "keyword": "故宫",
                                 "reason": "故宫是名胜古迹，拥有众多藏品",
                                 "cost": "1000.00",
                                 "travel": "火车"
                             },
                            {
                                 "city": "北京",
                                 "date": "2025-10-2"
                                 "keyword": "颐和园",
                                 "reason": "颐和园是著名公园",
                                 "cost": "500.00",
                                 "travel": "地铁"
                             }]
                         }
                         
                         示例2 (推荐一天行程)：
                         Q：我叫李四，想去在9月份看看南京，从上海出发，预算1千
                         A：
                         {
                             "title": "李四的南京一日游",
                             "content":
                                 [{
                                     "city": "南京",
                                     "date": "2025-9-1"
                                     "keyword": "中山陵",
                                     "reason": "中山陵是祭奠中国近代史重要伟人的陵墓，具有教育意义",
                                     "cost": "100.00",
                                     "travel": "高铁"
                                 }]
                         }
                         示例3 (跨城市旅行)：
                         Q：我想去日本旅行有什么推荐吗
                         A：
                         {
                         "title": "日本著名城市推荐",
                         "content":
                         [{
                             "city": "东京",
                             "date": "2025-10-1"
                             "keyword": "东京塔",
                             "reason": "东京塔具有代表性",
                             "cost": "1000.00",
                             "travel": "飞机"
                         },
                         {
                             "city": "东京",
                             "date": "2025-10-1"
                             "keyword": "晴空塔",
                             "reason": "对晴空塔感兴趣的可以去看看",
                             "cost": "50.00",
                             "travel": "飞机"
                         },
                        {
                             "city": "大阪",
                             "date": "2025-10-2"
                             "keyword": "天守阁",
                             "reason": "天守阁是大阪的标识性建筑",
                             "cost": "2000.00",
                             "travel": "新干线，地铁"
                         }]
                         }""")
                .build();
        Message userMsg = Message.builder()
                .role(Role.USER.getValue())
                .content(query)
                .build();
        ResponseFormat jsonMode = ResponseFormat.builder().type("json_object").build();
        GenerationParam param = GenerationParam.builder()
                // 若没有配置环境变量，请用阿里云百炼API Key将下行替换为：.apiKey("sk-xxx")
                .apiKey(llmProperty.getApiKey())
                .model(llmProperty.getModel())
                .messages(Arrays.asList(systemMsg, userMsg))
                .resultFormat(GenerationParam.ResultFormat.MESSAGE)
                .responseFormat(jsonMode)
                .build();
        return gen.call(param);
    }
}