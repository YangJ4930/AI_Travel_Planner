package com.travelplanner;

import java.util.Arrays;
import java.lang.System;
import java.util.List;

import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationParam;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.alibaba.dashscope.common.ResponseFormat;
import com.alibaba.dashscope.utils.Constants;

import com.travelplanner.dto.TravelPlanDto;
import com.travelplanner.wrapper.LLMTravelPlanWrapper;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class LLMTest {

    @Resource
    LLMTravelPlanWrapper llmTravelPlanWrapper;

    @Test
    void testDto(){
        List<TravelPlanDto> travelPlanDtoList = llmTravelPlanWrapper.llmPlan("我想去南京玩三天，预算3000，爱好是历史文化，给我多推荐两个景点和好一点的酒店");
        for(TravelPlanDto travelPlanDto: travelPlanDtoList){
            System.out.println(travelPlanDto.toString());
        }
    }


    public static GenerationResult callWithMessage() throws ApiException, NoApiKeyException, InputRequiredException {
        Generation gen = new Generation();
        Message systemMsg = Message.builder()
                .role(Role.SYSTEM.getValue())
                .content("""
                请从用户输入中定制相应的旅行的策略，并按照指定的JSON Schema格式输出：

                【输出格式要求】
                输出必须严格遵循以下JSON结构：
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
                
                【字段提取规则】
                1. city: 定制旅行的城市
                2. date: 定制旅行的日期
                3. keyword: 定制旅行的地点
                4. reason: 举出去那里旅行的原因
                5. cost: 旅行的开销
                6. travel: 给出交通工具，跨城可以给出飞机或者火车，同城可以用公交和高铁
                
                【参考示例】
                示例1：(推荐两天行程)
                Q：我想去带着孩子，从河北到北京玩两天有什么好推荐的？
                A：[{
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
                
                示例2 (推荐一天行程)：
                Q：我叫李四，想去在9月份看看南京，从上海出发，预算1千
                A：[{
                    "city": "南京",
                    "date": "2025-9-1"
                    "keyword": "中山陵",
                    "reason": "中山陵是祭奠中国近代史重要伟人的陵墓，具有教育意义",
                    "cost": "100.00",
                    "travel": "高铁"
                }]
                
                示例3 (跨城市旅行)：
                Q：我想去日本旅行有什么推荐吗
                A：[{
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
                }]""")
                .build();
        Message userMsg = Message.builder()
                .role(Role.USER.getValue())
                .content("我想去安徽玩玩，三个人，预算2000，有什么好的安排吗")
                .build();
        ResponseFormat jsonMode = ResponseFormat.builder().type("json_object").build();
        GenerationParam param = GenerationParam.builder()
                // 若没有配置环境变量，请用阿里云百炼API Key将下行替换为：.apiKey("sk-xxx")
                .apiKey("")
                .model("qwen-plus")
                .messages(Arrays.asList(systemMsg, userMsg))
                .resultFormat(GenerationParam.ResultFormat.MESSAGE)
                .responseFormat(jsonMode)
                .build();
        return gen.call(param);
    }

    @Test
    void testJson(){
        try {
            GenerationResult result = callWithMessage();
            System.out.println(result.getOutput().getChoices().get(0).getMessage().getContent());
        } catch (ApiException | NoApiKeyException | InputRequiredException e) {
            // 使用日志框架记录异常信息
            System.err.println("An error occurred while calling the generation service: " + e.getMessage());
        }
    }



}
