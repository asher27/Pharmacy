package com.example.project.pharmacy.service

import com.example.project.AbstractIntegrationContainerBaseTest
import com.example.project.pharmacy.entity.Pharmacy
import com.example.project.pharmacy.repository.PharmacyRepository
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Specification

class PharmacyRepositoryServiceTest extends AbstractIntegrationContainerBaseTest {

    @Autowired
    PharmacyRepositoryService pharmacyRepositoryService;

    @Autowired
    PharmacyRepository pharmacyRepository;

    def setup() {
        pharmacyRepository.deleteAll()
    }

    def "약국 주소변경"() {
        given:
        String address = "서울 특별시 성북구 종암동"
        String modifiedAddress = "서울 특별시 광진구 구의동"
        String name = "은혜 약국"
        double longitude = 128.11
        double latitude =  36.11

        def pharmacy = Pharmacy.builder()
                .pharmacyAddress(address)
                .pharmacyName(name)
                .longitude(longitude)
                .latitude(latitude)
                .build()

        when:
        def result = pharmacyRepository.save(pharmacy)
        pharmacyRepositoryService.updateAddress(result.getId(), modifiedAddress)

        then:
        def findPharmacy = pharmacyRepository.findById(result.getId()).orElse(null)
        findPharmacy.getPharmacyAddress() == modifiedAddress
    }

}
