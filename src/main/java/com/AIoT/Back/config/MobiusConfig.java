package com.AIoT.Back.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.UUID;

@Configuration
public class MobiusConfig {
    // Mobius 주소
    public static final String HOST = "https://onem2m.iotcoss.ac.kr";

    // 준이형이 준 프로젝트 정보
    public static final String AE_RN = "ae_name_sjusssms_01";
    public static final String CNT_RN = "stream";

    // 준이형이 준 키값 및 헤더 정보
    public static final String X_API_KEY = "sdWItrgAT1j4WNHD3prj8bCXEHu0RCbN";
    public static final String LECTURE_ID = "LCT_20250007";
    public static final String CREATOR_ID = "sjusssms";
    public static final String X_M2M_ORIGIN = "SOrigin_sjusssms";

    @Bean
    public RestTemplate mobiusRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();

        // 인터셉터 설정 (헤더 자동 추가)
        restTemplate.getInterceptors().add((request, body, execution) -> {
            HttpHeaders headers = request.getHeaders();

            // 공통 헤더 자동 주입
            headers.add("X-API-KEY", X_API_KEY);
            headers.add("X-AUTH-CUSTOM-LECTURE", LECTURE_ID);
            headers.add("X-AUTH-CUSTOM-CREATOR", CREATOR_ID);
            headers.add("X-M2M-Origin", X_M2M_ORIGIN);

            // 요청마다 ID 생성
            headers.add("X-M2M-RI", UUID.randomUUID().toString());

            // JSON 형식으로 통신
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

            return execution.execute(request, body);
        });

        return restTemplate;
    }
}
