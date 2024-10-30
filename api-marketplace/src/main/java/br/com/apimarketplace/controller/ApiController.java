package br.com.apimarketplace.controller;

import br.com.apimarketplace.dto.*;
import br.com.apimarketplace.model.Api;
import br.com.apimarketplace.model.Endpoint;
import br.com.apimarketplace.service.ApiCategoryService;
import br.com.apimarketplace.service.ApiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import br.com.apimarketplace.util.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/apis")
@Tag(name = "API Controller", description = "Realated API Controller")
public class ApiController {

    private ApiService apiService;
    private ApiCategoryService apiCategoryService;
    private JwtUtil jwtUtil;

    public ApiController(ApiService apiService, ApiCategoryService apiCategoryService, JwtUtil jwtUtil) {
        this.apiService = apiService;
        this.apiCategoryService = apiCategoryService;
        this.jwtUtil = jwtUtil;
    }

    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('PROVIDER')")
    @Operation(summary = "Create a new API", description = "A provider can create a new API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "API successfully created",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponseDto.class),
                            examples = @ExampleObject(value = "{\"id\": \"123\", \"message\": \"API created successfully\"}"))),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"error\": \"Invalid input\"}"))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"error\": \"Unexpected error occurred\"}")))
    })
    @PostMapping
    public ResponseEntity<Api> createApi(@RequestBody CreateApiDto createApiDto) {
        var apiId = apiService.createApi(createApiDto);
        return ResponseEntity.created(URI.create("/apis/" + apiId.toString())).build();
    }

    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('PROVIDER', 'CONSUMER')")
    @Operation(summary = "Find an API by Id", description = "A provider can find an API using the API id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponseDto.class),
                            examples = @ExampleObject(value = "{\"id\": \"3fa85f64-5717-4562-b3fc-2c963f66afa6\", \"name\": \"API name\", \"description\": \"API description\"}"))),
            @ApiResponse(responseCode = "404", description = "API not found",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"error\": \"API not found\"}"))),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"error\": \"Invalid API id\"}"))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"error\": \"Unexpected error occurred\"}")))
    })
    @GetMapping("/{apiId}")
    public ResponseEntity<ApiResponseDto> getApiById(@PathVariable("apiId") String apiId) {
        var api = apiService.getApiById(apiId);

        if (api.isPresent()) {
            return ResponseEntity.ok(api.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('PROVIDER', 'CONSUMER')")
    @Operation(summary = "List all APIs", description = "A provider can list all APIs")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponseDto.class),
                            examples = @ExampleObject(value = "[{\"id\": \"123\", \"name\": \"API 1\"}, {\"id\": \"456\", \"name\": \"API 2\"}]"))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"error\": \"Unexpected error occurred\"}")))
    })
    @GetMapping
    public ResponseEntity<List<ApiResponseDto>> listApis() {
        var apis = apiService.listAllApis();

        return ResponseEntity.ok(apis);
    }

    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('PROVIDER')")
    @Operation(summary = "Update an API by Id", description = "A provider can update an API by Id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "API successfully updated"),
            @ApiResponse(responseCode = "404", description = "API not found",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"error\": \"API not found\"}"))),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"error\": \"Invalid API id\"}"))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"error\": \"Unexpected error occurred\"}")))
    })
    @PutMapping("/{apiId}")
    public ResponseEntity<Void> updateApi(@PathVariable("apiId") String id, @RequestBody UpdateApiDto updateApiDto) {
        apiService.updateApiById(id, updateApiDto);
        return ResponseEntity.noContent().build();
    }

    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('PROVIDER')")
    @Operation(summary = "Delete an API by Id", description = "A provider can delete an API by Id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "API successfully deleted"),
            @ApiResponse(responseCode = "404", description = "API not found",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"error\": \"API not found\"}"))),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"error\": \"Invalid API id\"}"))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"error\": \"Unexpected error occurred\"}")))
    })
    @DeleteMapping("/{apiId}")
    public ResponseEntity<Void> deleteApi(@PathVariable("apiId") String id) {
        apiService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('PROVIDER')")
    @Operation(summary = "Create a new API category", description = "A provider can create a new API category")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "API category successfully created",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"id\": \"123\", \"message\": \"Category created successfully\"}"))),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"error\": \"Invalid input\"}"))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"error\": \"Unexpected error occurred\"}")))
    })
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

    @GetMapping("endpoint/{endpointId}/parameter")
    public ResponseEntity<List<ParameterDto>> getParametersByEndpoint(@PathVariable UUID endpointId) {
        List<ParameterDto> params = apiService.getParametersByEndpoint(endpointId);
        return ResponseEntity.ok(params);
    }

/*    @PutMapping("/endpoint/{endpointId}")
    public ResponseEntity<Void> updateEndpoint(@PathVariable("endpointId") UUID id, @RequestBody EndpointDto endpointDto) {
        apiService.updateEndpointById(id, endpointDto);
        System.out.println(endpointDto);
        return ResponseEntity.noContent().build();
    }*/

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

    @PostMapping("/access")
    public ResponseEntity<?> accessApi(
            @RequestHeader("Authorization") String token,
            @RequestParam("apiId") String apiId,
            @RequestParam("endpoint") String endpoint,
            @RequestParam(required = false) Map<String, String> queryParams) {

        // Extrair o token JWT
        String jwt = token.substring(7);  // Remove "Bearer " do token

        // Remover os queryParams indesejados (apiId e endpoint)
        queryParams.remove("apiId");
        queryParams.remove("endpoint");

        try {
            // Chama o serviço para redirecionar a chamada à API real com o endpoint correto
            String response = apiService.makeRequestToProviderApi(apiId, jwt, endpoint, queryParams);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao acessar a API");
        }
    }
}
