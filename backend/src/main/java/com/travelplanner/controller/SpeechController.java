package com.travelplanner.controller;

import com.travelplanner.common.Response;
import com.travelplanner.service.IatService;
import jakarta.annotation.Resource;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * Cloud STT controller – integrates XFYun IAT via backend.
 */
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/speech")
@RequiredArgsConstructor
@Slf4j
public class SpeechController {

    @Resource
    private IatService iatService;

    @PostMapping("/iat")
    public Response<String> iatRecognize(@RequestBody @Validated IatRequest req) {
        String text = iatService.recognizeShort(req.getAudioBase64());
        return Response.success(text);

    }

    @Data
    public static class IatRequest {
        /**
         * base64 string of 16kHz 16-bit PCM mono audio (without header)
         */
        private String audioBase64;
    }
}