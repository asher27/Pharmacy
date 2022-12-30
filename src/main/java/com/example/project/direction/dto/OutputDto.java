package com.example.project.direction.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OutputDto {

    private String pharmacyName;
    private String pharmacyAddress;
    private String directionUrl; // 길 안내 url
    private String roadViewUrl;  // 로드뷰 url
    private String distance;     // 고객주소와 약국주소의 거리
}
