package com.example.project.pharmacy.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PharmacyDto {

    private Long id;

    private String pharmacyName;
    private String pharmacyAddress;

    // 위도 (y)
    private double latitude;

    // 경도 (x)
    private double longitude;
}
