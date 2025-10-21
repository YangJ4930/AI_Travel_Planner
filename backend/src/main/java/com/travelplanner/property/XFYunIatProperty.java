package com.travelplanner.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "xfyun.iat")
public class XFYunIatProperty {
    private String appId;
    private String apiKey;
    private String apiSecret;
    private String hostUrl; // wss://iat-api.xfyun.cn/v2/iat
    private String format;  // audio/L16;rate=16000
    private String encoding; // raw
    private Integer vadEos; // 5000
    private String language; // zh_cn
    private String domain;   // iat
    private String accent;   // mandarin
}