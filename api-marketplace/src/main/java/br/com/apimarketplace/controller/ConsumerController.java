package br.com.apimarketplace.controller;

import br.com.apimarketplace.dto.ConsumerResponseDto;
import br.com.apimarketplace.dto.UpdateConsumerDto;
import br.com.apimarketplace.dto.UpdatePasswordInsiderDto;
import br.com.apimarketplace.model.Consumer;
import br.com.apimarketplace.repository.ConsumerRepository;
import br.com.apimarketplace.service.ConsumerService;
import br.com.apimarketplace.service.UpdatePasswordInsiderService;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/consumidores")
public class ConsumerController {

    @Autowired
    private ConsumerService consumerService;

    @Autowired
    private UpdatePasswordInsiderService updatePasswordInsiderService;

    @Autowired
    private ConsumerRepository consumerRepository;


    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('CONSUMER')")
    @Operation(summary = "Update a user as consumer", description = "A consumer update ")
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
    public ResponseEntity<Consumer> updateConsumer(@RequestBody UpdateConsumerDto updateConsumerDto) {
        var consumerId = consumerService.updateConsumer(updateConsumerDto);
        return ResponseEntity.created(URI.create("/consumidores/" + consumerId.toString())).build();
    }

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

    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('CONSUMER')")
    @Operation(summary = "Update a user password", description = "A consumer updates password after login")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Consumer password successfully updated",
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
            updatePasswordInsiderService.updatePasswordInsider(updatePasswordInsiderDto);
            return ResponseEntity.ok("Senha atualizada com sucesso");
        }catch (IllegalArgumentException e ){
           return ResponseEntity.badRequest().body(e.getMessage());
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao atualizar a senha");
        }
    }
}
