package com.travelplanner.service;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.travelplanner.common.enums.ResponseCodeEnum;
import com.travelplanner.common.enums.TravelPlannerException;
import com.travelplanner.property.XFYunIatProperty;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import okio.ByteString;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.codec.digest.HmacUtils;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * Minimal IAT service using XFYun WebSocket API for short audio.
 * Accepts base64 PCM16k (audio/L16;rate=16000) and returns recognized text.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class IatService {
    @Resource
    private XFYunIatProperty cfg;


    /**
     * Build authorized URL for IAT WebSocket per XFYun docs.
     */
    private String buildAuthUrl() {
        String hostUrl = cfg.getHostUrl(); // e.g. wss://iat-api.xfyun.cn/v2/iat
        String host = Objects.requireNonNull(HttpUrl.parse(hostUrl)).host();
        String date = rfc1123Date();
        String requestLine = "GET " + Objects.requireNonNull(HttpUrl.parse(hostUrl)).encodedPath() + " HTTP/1.1";
        String signatureOrigin = "host: " + host + "\n" + "date: " + date + "\n" + requestLine;
        String signatureSha = hmacSha256Base64(signatureOrigin, cfg.getApiSecret());
        String authOrigin = "api_key=\"" + cfg.getApiKey() + "\", algorithm=\"hmac-sha256\", headers=\"host date request-line\", signature=\"" + signatureSha + "\"";
        String authorization = Base64.encodeBase64String(StringUtils.getBytesUtf8(authOrigin));
        String url = hostUrl + "?authorization=" + urlEncode(authorization) + "&date=" + urlEncode(date) + "&host=" + urlEncode(host);
        return url;
    }

    private static String rfc1123Date() {
        SimpleDateFormat df = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'GMT'", Locale.US);
        df.setTimeZone(TimeZone.getTimeZone("GMT"));
        return df.format(new Date());
    }

    private static String hmacSha256Base64(String text, String secret) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec keySpec = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(keySpec);
            byte[] hmac = mac.doFinal(text.getBytes(StandardCharsets.UTF_8));
            return Base64.encodeBase64String(hmac);
        } catch (Exception e) {
            throw new RuntimeException("HMAC-SHA256计算失败", e);
        }
    }

    private static String urlEncode(String s) {
        return URLEncoder.encode(s, StandardCharsets.UTF_8);
    }

    /**
     * Perform recognition for a single short audio clip (base64 PCM16k).
     * @param audioBase64 base64 of 16k 16-bit PCM mono
     * @return recognized text
     */
    public String recognizeShort(String audioBase64) {
        try {
            OkHttpClient client = new OkHttpClient.Builder()
                    .readTimeout(60, TimeUnit.SECONDS)
                    .build();

            String url = buildAuthUrl();
            Request request = new Request.Builder().url(url).build();
            final StringBuilder resultText = new StringBuilder();
            final CountDownLatch done = new CountDownLatch(1);
            final String appId = cfg.getAppId();

            WebSocketListener listener = new WebSocketListener() {
                @Override
                public void onOpen(WebSocket webSocket, Response response) {
                    log.info("IAT WS opened");
                    // send first frame status=0 with config and first audio chunk
                    JSONObject frame = new JSONObject();
                    JSONObject common = new JSONObject();
                    common.put("app_id", appId);
                    JSONObject business = new JSONObject();
                    business.put("language", cfg.getLanguage());
                    business.put("domain", cfg.getDomain());
                    business.put("accent", cfg.getAccent());
                    business.put("vad_eos", cfg.getVadEos());
                    business.put("ptt", 0);
                    JSONObject data = new JSONObject();
                    data.put("status", 0);
                    data.put("format", cfg.getFormat());
                    data.put("encoding", cfg.getEncoding());
                    data.put("audio", audioBase64);
                    frame.put("common", common);
                    frame.put("business", business);
                    frame.put("data", data);
                    webSocket.send(JSON.toJSONString(frame));

                    // send last frame status=2
                    JSONObject last = new JSONObject();
                    last.put("data", new JSONObject().fluentPut("status", 2));
                    webSocket.send(JSON.toJSONString(last));
                }

                @Override
                public void onMessage(WebSocket webSocket, String text) {
                    try {
                        JSONObject obj = JSON.parseObject(text);
                        int code = obj.getIntValue("code");
                        if (code != 0) {
                            String msg = obj.getString("message");
                            log.error("IAT error code={} msg={}", code, msg);
                            done.countDown();
                            return;
                        }
                        JSONObject data = obj.getJSONObject("data");
                        if (data == null) return;
                        JSONObject result = data.getJSONObject("result");
                        if (result == null) return;
                        // parse ws -> cw -> w
                        StringBuilder sb = new StringBuilder();
                        for (Object wsObj : result.getJSONArray("ws")) {
                            JSONObject ws = (JSONObject) wsObj;
                            for (Object cwObj : ws.getJSONArray("cw")) {
                                JSONObject cw = (JSONObject) cwObj;
                                sb.append(cw.getString("w"));
                            }
                        }
                        if (sb.length() > 0) {
                            resultText.append(sb);
                        }
                    } catch (Exception e) {
                        log.error("IAT parse error", e);
                    }
                }

                @Override
                public void onClosing(WebSocket webSocket, int code, String reason) {
                    log.info("IAT WS closing code={} reason={}", code, reason);
                    webSocket.close(1000, null);
                    done.countDown();
                }

                @Override
                public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                    log.error("IAT WS failure", t);
                    done.countDown();
                }
            };

            WebSocket ws = client.newWebSocket(request, listener);
            // wait for completion or timeout
            boolean ok = done.await(30, TimeUnit.SECONDS);
            ws.close(1000, "client-close");
            return resultText.toString();
        }catch (InterruptedException e){
            log.error("IatService#recognizeShort, 翻译出错",e);
            throw new TravelPlannerException(ResponseCodeEnum.SYSTEM_ERROR, "翻译出错");
        }
    }
}