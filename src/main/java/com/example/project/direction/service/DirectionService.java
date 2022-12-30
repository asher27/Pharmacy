package com.example.project.direction.service;

import com.example.project.api.service.KakaoCategorySearchService;
import com.example.project.direction.entiry.Direction;
import com.example.project.direction.repository.DirectionRepository;
import com.example.project.api.dto.DocumentDto;
import com.example.project.pharmacy.service.PharmacySearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class DirectionService {

    private static int MAX_SEARCH_COUNT = 3; // 약국 최대 검색 개수
    private static double RADIUS_KM = 10.0; // 변경 10km 이내
    private static final String DIRECTION_BASE_URL = "https://map.kakao.com/link/map/";
    private final PharmacySearchService pharmacySearchService;
    private final DirectionRepository directionRepository;

    private final KakaoCategorySearchService kakaoCategorySearchService;

    private final Base62Service base62Service;

    public String findById(String encodedDirectionId) {
        Long decodeDirectionId = base62Service.decodeDirectionId(encodedDirectionId);
        Direction direction = directionRepository.findById(decodeDirectionId).orElse(null);

        // 유림약국, 232.232, 342.2332
        String params = String.join(",", direction.getTargetPharmacyName(), String.valueOf(direction.getTargetLatitude()),
                String.valueOf(direction.getTargetLongitude()));

        String result = UriComponentsBuilder.fromHttpUrl(DIRECTION_BASE_URL + params).toUriString();

        return result;
    }

    @Transactional
    public List<Direction> saveAll(List<Direction> directionList) {
        if (CollectionUtils.isEmpty(directionList)) return Collections.emptyList();

        return directionRepository.saveAll(directionList);
    }

    // DB 에서 조회
    public List<Direction> buildDirectionList(DocumentDto documentDto) {

        if (Objects.isNull(documentDto)) return Collections.emptyList();

        // 약국 데이터 조회, 고객과 약국 사이의 거리를 계산, 가까운 약국 sort
        return pharmacySearchService.searchPharmacyDtoList().stream()
                .map(pharmacyDtos -> Direction.builder()
                        .inputAddress(documentDto.getAddressName())
                        .inputLatitude(documentDto.getLatitude())
                        .inputLongitude(documentDto.getLongitude())
                        .targetPharmacyName(pharmacyDtos.getPharmacyName())
                        .targetAddress(pharmacyDtos.getPharmacyAddress())
                        .targetLatitude(pharmacyDtos.getLatitude())
                        .targetLongitude(pharmacyDtos.getLongitude())
                        .distance(calculateDistance(documentDto.getLatitude(), documentDto.getLongitude(),
                                pharmacyDtos.getLatitude(), pharmacyDtos.getLongitude()))
                        .build()
                )
                .filter(direction -> direction.getDistance() <= RADIUS_KM)
                .sorted(Comparator.comparing(Direction::getDistance))
                .limit(MAX_SEARCH_COUNT)
                .collect(Collectors.toList());

    }

    // Haversine formula
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        lat1 = Math.toRadians(lat1);
        lon1 = Math.toRadians(lon1);
        lat2 = Math.toRadians(lat2);
        lon2 = Math.toRadians(lon2);

        double earthRadius = 6371; //Kilometers
        return earthRadius * Math.acos(Math.sin(lat1) * Math.sin(lat2) + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon1 - lon2));
    }

    // CATEGORY API 에서 조회
    public List<Direction> buildDirectionListByCategoryApi(DocumentDto documentDto) {

        if (Objects.isNull(documentDto)) return Collections.emptyList();

        // CATEGORY API 활용 약국 데이터 조회, 고객과 약국 사이의 거리를 계산, 가까운 약국 sort
        return kakaoCategorySearchService.requestCategoryService(documentDto.getLatitude(), documentDto.getLongitude(), RADIUS_KM)
                .getDocumentDtoList().stream()
                .map(resultDocumentDto -> Direction.builder()
                        .inputAddress(documentDto.getAddressName())
                        .inputLatitude(documentDto.getLatitude())
                        .inputLongitude(documentDto.getLongitude())
                        .targetPharmacyName(resultDocumentDto.getPlaceName())
                        .targetAddress(resultDocumentDto.getAddressName())
                        .targetLatitude(resultDocumentDto.getLatitude())
                        .targetLongitude(resultDocumentDto.getLongitude())
                        .distance(resultDocumentDto.getDistance() * 0.001) // km 단위
                        .build()
                )
//                .filter(direction -> direction.getDistance() <= RADIUS_KM) // API 호출시 지정함, RADIUS_KM
//                .sorted(Comparator.comparing(Direction::getDistance))      // API 호출시 sort = distance로 저정하였음
                .limit(MAX_SEARCH_COUNT)
                .collect(Collectors.toList());
    }
}
