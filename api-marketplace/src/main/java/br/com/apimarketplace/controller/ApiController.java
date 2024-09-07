package br.com.apimarketplace.controller;

import br.com.apimarketplace.dto.CreateApiDto;
import br.com.apimarketplace.model.Api;
import br.com.apimarketplace.service.ApiService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/apis")
public class ApiController {

    private ApiService apiService;

    public ApiController(ApiService apiService) {
        this.apiService = apiService;
    }

    @PostMapping
    public ResponseEntity<Api> createApi(@RequestBody CreateApiDto createApiDto) {
        var apiId = apiService.createApi(createApiDto);
        return ResponseEntity.created(URI.create("/apis/" + apiId.toString())).build();
    }
}
