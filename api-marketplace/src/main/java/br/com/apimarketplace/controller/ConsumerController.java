package br.com.apimarketplace.controller;

import br.com.apimarketplace.dto.ConsumerResponseDto;
import br.com.apimarketplace.dto.CreateConsumerDto;
import br.com.apimarketplace.model.Consumer;
import br.com.apimarketplace.service.ConsumerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/consumidores")
public class ConsumerController {

    private ConsumerService consumerService;

    public ConsumerController(ConsumerService consumerService) {
        this.consumerService = consumerService;
    }

    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('CONSUMER')")
    @Operation(summary = "Create a user as consumer", description = "A consumer creates a user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Consumer successfully created",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"message\": \"Consumer created successfully\", \"consumerId\": \"12345\"}"))),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"error\": \"Invalid input\"}"))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"error\": \"Unexpected error occurred\"}")))
    })
    @PostMapping
    public ResponseEntity<Consumer> createConsumer(@RequestBody CreateConsumerDto createConsumerDto) {
        var consumerId = consumerService.createConsumer(createConsumerDto);
        return ResponseEntity.created(URI.create("/consumidores/" + consumerId.toString())).build();
    }/*VERIFICAR*/

    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('CONSUMER')")
    @Operation(summary = "Search consumer by Id", description = "Return specific details about a consumer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Consumer found",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"id\": \"12345\", \"username\": \"consumer_1\", \"email\": \"consumer_1@example.com\"}"))),
            @ApiResponse(responseCode = "404", description = "Consumer not found",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"error\": \"Consumer not found\"}"))),
            @ApiResponse(responseCode = "400", description = "Invalid consumer ID",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"error\": \"Invalid consumer ID format\"}"))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"error\": \"Unexpected error occurred\"}")))
    })
    @GetMapping("/{consumerId}")
    public ResponseEntity<ConsumerResponseDto> getConsumerById(@PathVariable("consumerId") String consumerId) {
        var consumer = consumerService.getConsumerById(consumerId);

        if (consumer.isPresent()) {
            return ResponseEntity.ok(consumer.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('CONSUMER')")
    @Operation(summary = "List all consumers", description = "Return a list of all consumers")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Consumers list retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "[{\"id\": \"12345\", \"username\": \"consumer_1\", \"email\": \"consumer_1@example.com\"}, {\"id\": \"67890\", \"username\": \"consumer_2\", \"email\": \"consumer_2@example.com\"}]"))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"error\": \"Unexpected error occurred\"}")))
    })
    @GetMapping
    public ResponseEntity<List<ConsumerResponseDto>> listConsumers() {
        var consumers = consumerService.listAllConsumers();

        return ResponseEntity.ok(consumers);
    }
}
