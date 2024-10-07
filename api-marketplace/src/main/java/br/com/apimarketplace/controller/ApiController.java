package br.com.apimarketplace.controller;

import br.com.apimarketplace.dto.ApiResponseDto;
import br.com.apimarketplace.dto.CreateApiCategory;
import br.com.apimarketplace.dto.CreateApiDto;
import br.com.apimarketplace.dto.UpdateApiDto;
import br.com.apimarketplace.model.Api;
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
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@Tag(name = "API Controller", description = "Realated API Controller")
@Validated
@RestController
@RequestMapping("/apis")
public class ApiController {

    private final ApiService apiService;
    private final ApiCategoryService apiCategoryService;

    public ApiController(ApiService apiService, ApiCategoryService apiCategoryService) {
        this.apiService = apiService;
        this.apiCategoryService = apiCategoryService;
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
    public ResponseEntity<Api> createApi(@RequestBody @Valid CreateApiDto createApiDto){
        var apiId = apiService.createApi(createApiDto);
        return ResponseEntity.created(URI.create("/apis/" + apiId.toString())).build();
    }

    @SecurityRequirement(name = "bearerAuth")
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
    public ResponseEntity<ApiResponseDto> getApiById(@PathVariable("apiId") @NotBlank String apiId) {
        var api = apiService.getApiById(apiId);
        return api.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('CONSUMER')")
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
    public ResponseEntity<Void> updateApi(@PathVariable("apiId") String id, @RequestBody @Valid UpdateApiDto updateApiDto) {
        apiService.updateApiById(id, updateApiDto);
        return ResponseEntity.noContent().build();
    }

    @SecurityRequirement(name = "bearerAuth")
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
    public ResponseEntity<Void> deleteApi(@PathVariable("apiId") @NotBlank String id) {
        apiService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @SecurityRequirement(name = "bearerAuth")
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
    public ResponseEntity<Api> createApiCategory(@RequestBody @Valid CreateApiCategory createApiCategory) {
        var apiCategoryId = apiCategoryService.createCategory(createApiCategory);
        return ResponseEntity.created(URI.create("/apis/categorias" + apiCategoryId.toString())).build();
    }
}