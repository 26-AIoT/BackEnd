package com.AIoT.Back.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

public class MobiusDtos {

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Notification {
        @JsonProperty("m2m:sgn")
        private Sgn m2mSgn;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Sgn {
        private Nev nev;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Nev {
        private Rep rep;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Rep {
        @JsonProperty("m2m:cin")
        private M2mCin m2mCin;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class M2mCin {
        private Content con;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Content {
        private List<Double> vec;
    }
}