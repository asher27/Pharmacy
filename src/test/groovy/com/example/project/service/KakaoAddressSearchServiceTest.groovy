package com.example.project.service

import com.example.project.AbstractIntegrationContainerBaseTest
import com.example.project.api.service.KakaoAddressSearchService
import org.springframework.beans.factory.annotation.Autowired

class KakaoAddressSearchServiceTest extends AbstractIntegrationContainerBaseTest {

    @Autowired
    private KakaoAddressSearchService kakaoAddressSearchService;

    def "null 처리 검증"() {
        given:
        String address = null;

        when:
        def result = kakaoAddressSearchService.requestAddressService(address)

        then:
        result == null
    }

    def "정상 documents 반환"() {
        given:
        String address = "서울 성북구 종암로 10길"

        when:
        def result = kakaoAddressSearchService.requestAddressService(address)

        then:
        result.documentDtoList.size() > 0
        result.metaDto.totalCount > 0
        result.documentDtoList.get(0).addressName != null
    }

    def "정상적 주소 입력, 정상적 위도, 경도 변환"() {
        given:
        boolean actualResult = false

        when:
        def searchResult = kakaoAddressSearchService.requestAddressService(inputAddress)

        then:
        if (searchResult == null) actualResult = false
        else actualResult = searchResult.getDocumentDtoList().size() > 0

        actualResult == expectedResult


        where:
        inputAddress                            | expectedResult
        "서울 특별시 성북구 종암동"                 | true
        "서울 성북구 종암동 91"                    | true
        "서울 대학로"                             | true
        "서울 성북구 종암동 잘못된 주소"             | false
        "광진구 구의동 251-45"                     | true
        "광진구 구의동 251-455555"                 | false
        ""                                       | false

    }

}
