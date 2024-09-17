package com.ead.course.clients;

import com.ead.course.controllers.dtos.ResponsePageDto;
import com.ead.course.controllers.dtos.UserDto;
import com.ead.course.services.UtilsService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.UUID;
@Log4j2
@Component
public class AuthUserClient {

    private final RestTemplate restTemplate;
    private final UtilsService utilsService;

    @Value("${ead.api.url.authuser}")
    private String REQUEST_URL_AUTHUSER;

    public AuthUserClient(RestTemplate restTemplate, UtilsService utilsService) {
        this.restTemplate = restTemplate;
        this.utilsService = utilsService;
    }

    public Page<UserDto> getAllUsersByCourse(UUID courseId, Pageable pageable) {
        List<UserDto> searchResult = null;
        ResponseEntity<ResponsePageDto<UserDto>> result = null;
        String url = this.utilsService.createUrl(courseId, pageable);
        log.debug("Resquest URL: {}", url);
        log.info("Resquest URL: {}", url);
        try {
            ParameterizedTypeReference<ResponsePageDto<UserDto>> responseType = new ParameterizedTypeReference<ResponsePageDto<UserDto>>() {};
            result = this.restTemplate.exchange(url, HttpMethod.GET, null, responseType);
            searchResult = result.getBody().getContent();
            log.debug("Response Numer of Elements: {}", searchResult.size());
        } catch (HttpStatusCodeException e) {
            log.error("Error request /courses {}", e);
        }
        log.info("Ending request /users courseId{} ", courseId);
        return  result.getBody();
    }

    public ResponseEntity<UserDto> getOneUserById(UUID userId) {
        String url = REQUEST_URL_AUTHUSER + "/users/"+userId;
        return this.restTemplate.exchange(url, HttpMethod.GET, null, UserDto.class);
    }
}
