package com.AIoT.Back.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

public class MobiusDtos {
    // Mobius가 보내주는 전체 알림 구조 (oneM2M 표준)
    @Data
    public static class Notification {
        @JsonProperty("m2m:sgn")
        private Sgn sgn;
    }

    @Data
    public static class Sgn {
        private Nev nev;
    }

    @Data
    public static class Nev {
        private Rep rep;
    }

    @Data
    public static class Rep {
        @JsonProperty("m2m:cin")
        private Cin cin;
    }

    @Data
    public static class Cin {
        private String con; // 여기가 진짜 데이터 (JSON String) 들어있는 곳!
    }
}
