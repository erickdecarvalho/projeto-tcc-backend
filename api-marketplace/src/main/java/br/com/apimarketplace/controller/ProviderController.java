package br.com.apimarketplace.controller;

import br.com.apimarketplace.dto.CreateProviderDto;
import br.com.apimarketplace.dto.ProviderResponseDto;
import br.com.apimarketplace.model.Provider;
import br.com.apimarketplace.service.ProviderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/provedores")
@Validated
@Tag(name = "Provider Controller", description = "Related Provider Controller")
public class ProviderController {

    private ProviderService providerService;

    public ProviderController(ProviderService providerService) {
        this.providerService = providerService;
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Create a user as provider", description = "A provider creates a user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Provider successfully created",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"message\": \"Provider created successfully\", \"providerId\": \"12345\"}"))),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"error\": \"Invalid input\"}"))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"error\": \"Unexpected error occurred\"}")))
    })
    @PostMapping
    public ResponseEntity<Provider> createProvider(@RequestBody @Valid CreateProviderDto createProviderDto) {
        var providerId = providerService.createProvider(createProviderDto);
        return ResponseEntity.created(URI.create("/provedores/" + providerId.toString())).build();
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Search provider by Id", description = "Return specific details about a provider")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Provider found",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"id\": \"12345\", \"organizationName\": \"Provider Co.\", \"email\": \"provider@example.com\"}"))),
            @ApiResponse(responseCode = "404", description = "Provider not found",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"error\": \"Provider not found\"}"))),
            @ApiResponse(responseCode = "400", description = "Invalid provider ID",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"error\": \"Invalid provider ID format\"}"))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"error\": \"Unexpected error occurred\"}")))
    })
    @GetMapping("/{providerId}")
    public ResponseEntity<ProviderResponseDto> getProviderById(@PathVariable("providerId") String providerId) {
        var provider = providerService.getProviderById(providerId);
        return provider.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "List all providers", description = "Return a list of all providers")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Providers list retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "[{\"id\": \"12345\", \"organizationName\": \"Provider Co.\", \"email\": \"provider@example.com\"}, {\"id\": \"67890\", \"organizationName\": \"Another Provider Inc.\", \"email\": \"another@example.com\"}]"))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"error\": \"Unexpected error occurred\"}")))
    })
    @GetMapping
    public ResponseEntity<List<ProviderResponseDto>> listProviders() {
        var providers = providerService.listAllProviders();
        return ResponseEntity.ok(providers);
    }
}