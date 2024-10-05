package br.com.apimarketplace.controller;

import br.com.apimarketplace.dto.ApiResponseDto;
import br.com.apimarketplace.dto.CreateApiCategory;
import br.com.apimarketplace.dto.CreateApiDto;
import br.com.apimarketplace.dto.UpdateApiDto;
import br.com.apimarketplace.model.Api;
import br.com.apimarketplace.service.ApiCategoryService;
import br.com.apimarketplace.service.ApiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
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

    @PostMapping
    @Operation(summary = "Find an API by Id", description = "A provider can find an API using the API id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Api> createApi(@RequestBody @Valid CreateApiDto createApiDto){
            var apiId = apiService.createApi(createApiDto);
            return ResponseEntity.created(URI.create("/apis/" + apiId.toString())).build();
    }

    @GetMapping("/{apiId}")
    @Operation(summary = "Find an API by Id", description = "A provider can find an API using the API id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "404", description = "API not found"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ApiResponseDto> getApiById(@PathVariable("apiId") @NotBlank String apiId) {
        var api = apiService.getApiById(apiId);
        return api.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }


    @GetMapping
    @Operation(summary = "Find an API by Id", description = "A provider can find an API using the API id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<ApiResponseDto>> listApis() {
        var apis = apiService.listAllApis();
        return ResponseEntity.ok(apis);
    }

    @PutMapping("/{apiId}")
    @Operation(summary = "Update an API by Id", description = "A provider can update a API in User control")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "success created"),
            @ApiResponse(responseCode = "404", description = "API not found"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> updateApi(@PathVariable("apiId") String id, @RequestBody @Valid UpdateApiDto updateApiDto) {
        apiService.updateApiById(id, updateApiDto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{apiId}")
    @Operation(summary = "Delete an API by Id", description = "A provider can delete a API in User control")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "success created"),
            @ApiResponse(responseCode = "404", description = "API not found"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> deleteApi(@PathVariable("apiId") @NotBlank String id) {
        apiService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/categorias")
    @Operation(summary = "Update an API by Id", description = "A provider can update a API in User control")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "success created"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Api> createApiCategory(@RequestBody @Valid CreateApiCategory createApiCategory) {
        var apiCategoryId = apiCategoryService.createCategory(createApiCategory);
        return ResponseEntity.created(URI.create("/apis/categorias" + apiCategoryId.toString())).build();
    }
}
