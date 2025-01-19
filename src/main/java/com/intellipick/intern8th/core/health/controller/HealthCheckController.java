package com.intellipick.intern8th.core.health.controller;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/health")
@Tag(name = "HealthCheckController - 헬스체크 컨트롤러",
        description = "서버의 상태를 확인하기 위한 API를 제공하는 컨트롤러입니다.")
public class HealthCheckController {

    @Schema(name = "healthCheck - 헬스체크",
            description = "로드밸런싱 헬스 체크용입니다.")
    @GetMapping
    public String healthCheck() {
        return "pong";
    }
}
