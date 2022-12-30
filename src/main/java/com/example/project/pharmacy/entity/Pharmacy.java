package com.example.project.pharmacy.entity;


import com.example.project.BaseTimeEntity;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Pharmacy extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String pharmacyName;
    private String pharmacyAddress;

    // 위도 (y)
    private double latitude;

    // 경도 (x)
    private double longitude;


    public void changePharmacyAddress(String address) {
        this.pharmacyAddress = address;
    }
}
