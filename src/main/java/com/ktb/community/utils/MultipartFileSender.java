package com.ktb.community.utils;

import com.ktb.community.exception.FileSendFailedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class MultipartFileSender {

    private final WebClient webClient;

    public String sendFile(MultipartFile file, String uri) {
        if (file == null)
            throw new IllegalArgumentException(ExceptionMessageConst.ILLEGAL_FILE);

        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("file", file.getResource());

        ResponseEntity<Map<String, String>> response = webClient.post()
                .uri(uri)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(builder.build()))
                .retrieve()
                .toEntity(new ParameterizedTypeReference<Map<String, String>>() {
                })
                .block();

        if (response == null || !response.getStatusCode().is2xxSuccessful())
            throw new FileSendFailedException();

        Map<String, String> responseMap = response.getBody();
        return responseMap.get("imageName");
    }
}
