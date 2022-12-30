package com.example.project.pharmacy.cache;


import com.example.project.pharmacy.dto.PharmacyDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class PharmacyRedisTemplateService {

    private static final String CACHE_KEY = "PHARMACY";
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    private HashOperations<String, String, String> hashOperations;

    @PostConstruct
    public void init() {
        this.hashOperations = redisTemplate.opsForHash();
    }

    public void save(PharmacyDto pharmacyDto) {
        if (Objects.isNull(pharmacyDto) || Objects.isNull(pharmacyDto.getId())) {
            log.error("Required Value must not be null!");
            return;
        }

        try {

            hashOperations.put(CACHE_KEY, pharmacyDto.getId().toString(), serializePharmacyDto(pharmacyDto));
            log.info("[PharmacyRedisTemplateService save] success id : {}, value : {}", pharmacyDto.getId(), serializePharmacyDto(pharmacyDto));
        } catch (Exception e) {
            log.error("[PharmacyRedisTemplateService save] error : {}", e.getMessage());
        }
    }

    public List<PharmacyDto> findAll() {

        try {

            List<PharmacyDto> list = new ArrayList<>();
            Collection<String> values = hashOperations.entries(CACHE_KEY).values();
            for (String value : values) {
                list.add(deSerializePharmacyDto(value));
            }
            return list;

        } catch (Exception e) {
            log.error("[PharmacyRedisTemplateService findAll] error : {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    public void delete(Long id) {
        hashOperations.delete(CACHE_KEY, String.valueOf(id));
        log.info("[PharmacyRedisTemplateService delete] id : {}", id);
    }


    public String serializePharmacyDto(PharmacyDto pharmacyDto) throws JsonProcessingException {
        return objectMapper.writeValueAsString(pharmacyDto);
    }

    public PharmacyDto deSerializePharmacyDto(String value) throws JsonProcessingException {
        return objectMapper.readValue(value, PharmacyDto.class);
    }


}
