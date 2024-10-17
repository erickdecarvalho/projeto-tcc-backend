package br.com.apimarketplace.controller;

import br.com.apimarketplace.dto.*;
import br.com.apimarketplace.model.Api;
import br.com.apimarketplace.model.Endpoint;
import br.com.apimarketplace.service.ApiCategoryService;
import br.com.apimarketplace.service.ApiService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/apis")
public class ApiController {

    private ApiService apiService;
    private ApiCategoryService apiCategoryService;

    public ApiController(ApiService apiService, ApiCategoryService apiCategoryService) {
        this.apiService = apiService;
        this.apiCategoryService = apiCategoryService;
    }

    @PostMapping
    public ResponseEntity<Api> createApi(@RequestBody CreateApiDto createApiDto) {
        var apiId = apiService.createApi(createApiDto);
        return ResponseEntity.created(URI.create("/apis/" + apiId.toString())).build();
    }

    @GetMapping("/{apiId}")
    public ResponseEntity<ApiResponseDto> getApiById(@PathVariable("apiId") String apiId) {
        var api = apiService.getApiById(apiId);

        if (api.isPresent()) {
            return ResponseEntity.ok(api.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<ApiResponseDto>> listApis() {
        var apis = apiService.listAllApis();

        return ResponseEntity.ok(apis);
    }

    @PutMapping("/{apiId}")
    public ResponseEntity<Void> updateApi(@PathVariable("apiId") String id, @RequestBody UpdateApiDto updateApiDto) {
        apiService.updateApiById(id, updateApiDto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{apiId}")
    public ResponseEntity<Void> deleteApi(@PathVariable("apiId") String id) {
        apiService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/categorias")
    public ResponseEntity<Api> createApiCategory(@RequestBody CreateApiCategory createApiCategory) {
        var apiCategoryId = apiCategoryService.createCategory(createApiCategory);
        return ResponseEntity.created(URI.create("/apis/categorias" + apiCategoryId.toString())).build();
    }

    // Criação de endpoint com o ID da API na URL
    @PostMapping("/endpoint/{apiId}")
    public ResponseEntity<Endpoint> createEndpoint(
            @PathVariable UUID apiId,  // O ID da API é passado como parâmetro
            @RequestBody EndpointDto endpointDto) {
        Endpoint endpoint = apiService.createEndpoint(apiId, endpointDto);
        return ResponseEntity.ok(endpoint);
    }

    // Endpoint para listar todos os endpoints de uma API
    @GetMapping("/endpoints/{apiId}")
    public ResponseEntity<List<EndpointDto>> getEndpointsByApi(@PathVariable UUID apiId) {
        List<EndpointDto> endpoints = apiService.getEndpointsByApi(apiId);
        return ResponseEntity.ok(endpoints);
    }

    @PostMapping("/endpoint/{endpointId}/parameter")
    public ResponseEntity<Void> addParamToEndpoint(
            @PathVariable UUID endpointId,
            @RequestBody ParameterDto parameterDto) {
        apiService.addParamToEndpoint(endpointId, parameterDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/endpoint/{endpointId}/parameter")
    public ResponseEntity<List<ParameterDto>> getParametersByEndpoint(@PathVariable UUID endpointId) {
        List<ParameterDto> params = apiService.getParametersByEndpoint(endpointId);
        return ResponseEntity.ok(params);
    }

    @PutMapping("/endpoint/{endpointId}")
    public ResponseEntity<Void> updateEndpoint(@PathVariable("endpointId") UUID id, @RequestBody EndpointDto endpointDto) {
        apiService.updateEndpointById(id, endpointDto);
        System.out.println(endpointDto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/endpoint/{endpointId}")
    public ResponseEntity<Void> deleteEndpoint(@PathVariable("endpointId") UUID id) {
        apiService.deleteEndpointById(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/endpoint/parameter/{parameterId}")
    public ResponseEntity<Void> deleteParameterById(@PathVariable("parameterId") UUID id) {
        apiService.deleteParameterById(id);
        return ResponseEntity.noContent().build();
    }
}
