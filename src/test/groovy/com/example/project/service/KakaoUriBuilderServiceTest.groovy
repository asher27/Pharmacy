package com.example.project.service

import com.example.project.api.service.KakaoUriBuilderService
import spock.lang.Specification

import java.nio.charset.StandardCharsets

class KakaoUriBuilderServiceTest extends Specification {

    private KakaoUriBuilderService uriBuilderService;

    def setup() {
        uriBuilderService = new KakaoUriBuilderService()
    }

    def "builderUriByAddressSearch - 한글 파라미터의 경우 정상적으로 인코딩"() {
        given:
        String address = "서울 성북구"
        def utf_8 = StandardCharsets.UTF_8

        when:
        def uri = uriBuilderService.builderUriByAddressSearch(address)
        def decodedResult = URLDecoder.decode(uri.toString(), utf_8)

        then:
        decodedResult == "https://dapi.kakao.com/v2/local/search/address.json?query=서울 성북구"
    }


}
