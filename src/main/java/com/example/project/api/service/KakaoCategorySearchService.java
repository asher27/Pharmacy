package com.example.project.api.service;

import com.example.project.api.dto.KakaoApiResponseDto;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

@Slf4j
@RequiredArgsConstructor
@Service
public class KakaoCategorySearchService {

    private final RestTemplate restTemplate;

    private final KakaoUriBuilderService kakaoUriBuilderService;

    private static final String PHARMACY_CATEGORY = "PM9";

    @Value("${kakao.rest.api.key}")
    private String kakaoRestApiKey;

    @Retryable(
            value = {RuntimeException.class},
            maxAttempts = 2,
            backoff = @Backoff(delay = 2000)
    )
    public KakaoApiResponseDto requestCategoryService(double latitude, double longitude, double radius) {

        URI uri = kakaoUriBuilderService.builderUriByCategorySearch(latitude, longitude, radius, PHARMACY_CATEGORY);

        // Header
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(HttpHeaders.AUTHORIZATION, "KakaoAK " + kakaoRestApiKey);

        // HttpEntity
        HttpEntity httpEntity = new HttpEntity<>(httpHeaders);

        ResponseEntity<KakaoApiResponseDto> exchange = restTemplate.exchange(uri, HttpMethod.GET, httpEntity, KakaoApiResponseDto.class);
        KakaoApiResponseDto body = exchange.getBody();
        return body;
    }

    @Recover
    public KakaoApiResponseDto recover(RuntimeException e, double latitude, double longitude, double radius) {
        log.error("all retries failed. latitude = {}, longitude = {}, radius = {}, error = {}", latitude, longitude, radius, e.getMessage());
        return null;
    }
}
