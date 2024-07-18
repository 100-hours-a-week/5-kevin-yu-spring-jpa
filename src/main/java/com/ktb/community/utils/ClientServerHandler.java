package com.ktb.community.utils;

import com.ktb.community.exception.DeleteImageFailedException;
import com.ktb.community.exception.FileSendFailedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Slf4j
@Component
public class ClientServerHandler {

    private final WebClient webClient;

    public ClientServerHandler(ClientHttpConnector clientHttpConnector) {
        this.webClient = WebClient.builder()
                .baseUrl("http://localhost:3000")
                .clientConnector(clientHttpConnector)
                .build();
    }

    public String sendFile(MultipartFile file, String prevImage, String uri, ClientServerHandlerMethod method) {
        if (file == null)
            throw new IllegalArgumentException(ExceptionMessageConst.ILLEGAL_FILE);

        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("file", file.getResource());
        builder.part("prevImageName", prevImage);

        WebClient.RequestBodyUriSpec request = switch (method) {
            case PUT -> webClient.put();
            case POST -> webClient.post();
            case PATCH -> webClient.patch();
        };

        ResponseEntity<Map<String, String>> response = request
                .uri(uri)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(builder.build()))
                .retrieve()
                .toEntity(new ParameterizedTypeReference<Map<String, String>>() {})
                .block();

        if (response == null || !response.getStatusCode().is2xxSuccessful())
            throw new FileSendFailedException();

        Map<String, String> responseMap = response.getBody();
        return responseMap.get("imageName");
    }

    public HttpStatus requestDeleteFile(String uri, String imageName) {
        uri += "?image=" + imageName;

        ResponseEntity<Void> responseEntity = webClient.delete()
                .uri(uri)
                .retrieve()
                .toBodilessEntity()
                .block();

        if(responseEntity == null)
            throw new DeleteImageFailedException();

        return HttpStatus.valueOf(responseEntity.getStatusCode().value());
    }
}
