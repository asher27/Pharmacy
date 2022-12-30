package com.example.project.api.service;


import com.example.project.api.dto.KakaoApiResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

@Slf4j
@RequiredArgsConstructor
@Service
public class KakaoAddressSearchService {

    private final RestTemplate restTemplate;
    private final KakaoUriBuilderService uriBuilderService;
    @Value("${kakao.rest.api.key}")
    private String kakaoRestApiKey;

    @Retryable(
            value = {RuntimeException.class},
            maxAttempts = 2,
            backoff = @Backoff(delay = 2000)
    )
    public KakaoApiResponseDto requestAddressService(String address) {

        if (ObjectUtils.isEmpty(address)) {
            return null;
        }

        /**
         * kakao api 호출
         */

        // uri 생성
        URI uri = uriBuilderService.builderUriByAddressSearch(address);
        // header 생성하여 authentication 삽입
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(HttpHeaders.AUTHORIZATION, "KakaoAK " + kakaoRestApiKey);
        // header를 담은 httpEntity 생성
        HttpEntity httpEntity = new HttpEntity<>(httpHeaders);
        KakaoApiResponseDto body =
                restTemplate.exchange(uri, HttpMethod.GET, httpEntity, KakaoApiResponseDto.class)
                        .getBody();

        return body;
    }

    @Recover
    public KakaoApiResponseDto recover(RuntimeException e, String address) {
        log.error("all retries failed. address = {}, error = {}", address, e.getMessage());
        return null;
    }
}
