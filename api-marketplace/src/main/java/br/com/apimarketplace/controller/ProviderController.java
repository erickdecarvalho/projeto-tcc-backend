package br.com.apimarketplace.controller;

import br.com.apimarketplace.dto.*;
import br.com.apimarketplace.model.Provider;
import br.com.apimarketplace.repository.ProviderRepository;
import br.com.apimarketplace.service.ProviderService;
import br.com.apimarketplace.service.UpdatePasswordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/provedores")
public class ProviderController {

    @Autowired
    private ProviderService providerService;

    @Autowired
    private UpdatePasswordService updatePasswordService;

    @Autowired
    private ProviderRepository providerRepository;



    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('PROVIDER')")
    @Operation(summary = "Update a user as provider", description = "A provider update")
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
    public ResponseEntity<Provider> updateProvider(@RequestBody @Valid UpdateProviderDto updateProviderDto) {
        var providerId = providerService.updateProvider(updateProviderDto);
        return ResponseEntity.created(URI.create("/provedores/" + providerId.toString())).build();
    }

    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('PROVIDER')")
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

        if (provider.isPresent()) {
            return ResponseEntity.ok(provider.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('PROVIDER')")
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

    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('PROVIDER')")
    @Operation(summary = "Provider post Api", description = "A provider can post a API")
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
    @PostMapping("/api/{providerId}")
    public ResponseEntity<?> postApi(@PathVariable UUID providerId, @RequestBody CreateApiDto createApiDto) throws IOException {
        boolean success = providerService.postApi(providerId, createApiDto);
        if (success) {
            return ResponseEntity.status(HttpStatus.OK).build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('PROVIDER')")
    @Operation(summary = "List all API's", description = "Return a list of all API from a provider")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Providers list retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "[{\"id\": \"12345\", \"organizationName\": \"Provider Co.\", \"email\": \"provider@example.com\"}, {\"id\": \"67890\", \"organizationName\": \"Another Provider Inc.\", \"email\": \"another@example.com\"}]"))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"error\": \"Unexpected error occurred\"}")))
    })
    @GetMapping("/apis/{providerId}")
    public ResponseEntity<?> getAllApisByUserId(@PathVariable UUID providerId) {
        List<ApiDto> apis = providerService.getAllApis(providerId);
        return ResponseEntity.ok(apis);
    }

    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('PROVIDER')")
    @Operation(summary = "List a providers API", description = "Return a list of all providers API by providersID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Providers list retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "[{\"id\": \"12345\", \"organizationName\": \"Provider Co.\", \"email\": \"provider@example.com\"}, {\"id\": \"67890\", \"organizationName\": \"Another Provider Inc.\", \"email\": \"another@example.com\"}]"))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"error\": \"Unexpected error occurred\"}")))
    })
    @GetMapping("/api/{apiId}")
    public ResponseEntity<?> getApiById(@PathVariable UUID apiId) {
        ApiDto apiDto = providerService.getApiById(apiId);
        if (apiDto != null) {
            return ResponseEntity.ok(apiDto);
        }
        else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('PROVIDER')")
    @Operation(summary = "Update a user password", description = "A provider updates password after login")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Provider password successfully updated",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"message\": \"Consumer password updated successfully\"}"))),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"error\": \"Invalid input\"}"))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"error\": \"Unexpected error occurred\"}")))
    })
    @PutMapping("/reset")
    public ResponseEntity<?> updateInsidePassword(@RequestBody @Valid UpdatePasswordInsiderDto updatePasswordInsiderDto) {
        try{
            updatePasswordService.updatePasswordInsider(updatePasswordInsiderDto);
            return ResponseEntity.ok("Senha atualizada com sucesso");
        }catch (IllegalArgumentException e ){
            return ResponseEntity.badRequest().body(e.getMessage());
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao atualizar a senha");
        }
    }
}
