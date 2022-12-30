package com.example.project.api.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Slf4j
@Service
public class KakaoUriBuilderService {

    private static final String KAKAO_LOCAL_SEARCH_ADDRESS_URL = "https://dapi.kakao.com/v2/local/search/address.json";
    private static final String KAKAO_LOCAL_CATEGORY_SEARCH_URL = "https://dapi.kakao.com/v2/local/search/category.json";
//    https://dapi.kakao.com/v2/local/search/category.json?category_group_code=PM9&radius=10000&x=126.97774853256885&y=37.56077835029789&sort=distance
    public URI builderUriByAddressSearch(String address) {

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(KAKAO_LOCAL_SEARCH_ADDRESS_URL);
        uriBuilder.queryParam("query", address);

        URI uri = uriBuilder.build()
                .encode()
                .toUri();
        log.info("[KakaoUriBuilderService builderUriByAddressSearch] address: {}, uri: {}", address, uri);

        return uri;
    }

    public URI builderUriByCategorySearch(double latitude, double longitude, double radius, String category) {

        double meterRadius = radius * 1000;

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(KAKAO_LOCAL_CATEGORY_SEARCH_URL);
        uriBuilder.queryParam("category_group_code", category);
        uriBuilder.queryParam("x", longitude);
        uriBuilder.queryParam("y", latitude);
        uriBuilder.queryParam("radius", meterRadius);
        uriBuilder.queryParam("sort", "distance");

        URI uri = uriBuilder.build()
                .encode()
                .toUri();
        log.info("[KakaoUriBuilderService builderUriByCategorySearch] category: {}, latitude: {}, longitude: {}, radius: {}, uri: {}", category, latitude, longitude, radius, uri);

        return uri;
    }
}
