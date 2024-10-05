package br.com.apimarketplace.controller;

import br.com.apimarketplace.dto.ConsumerResponseDto;
import br.com.apimarketplace.dto.CreateConsumerDto;
import br.com.apimarketplace.model.Consumer;
import br.com.apimarketplace.service.ConsumerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@Tag(name = "Consumer Controller", description = "Realated Consumer Controller")
@RestController
@RequestMapping("/consumidores")
public class ConsumerController {

    private ConsumerService consumerService;

    public ConsumerController(ConsumerService consumerService) {
        this.consumerService = consumerService;
    }

    @PostMapping
    @Operation(summary = "Create a user as consumer", description = "A consumer create user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Consumer> createConsumer(@RequestBody CreateConsumerDto createConsumerDto) {
        var consumerId = consumerService.createConsumer(createConsumerDto);
        return ResponseEntity.created(URI.create("/consumidores/" + consumerId.toString())).build();
    }

    @GetMapping("/{consumerId}")
    @Operation(summary = "Search consumer by Id", description = "Return specific details about consumer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sucess"),
            @ApiResponse(responseCode = "404", description = "Not Found"),
            @ApiResponse(responseCode = "400", description = "Invalid id consumer"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ConsumerResponseDto> getConsumerById(@PathVariable("consumerId") String consumerId) {
        var consumer = consumerService.getConsumerById(consumerId);
        return consumer.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    @Operation(summary = "Search consumer", description = "Return specific details about consumer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sucess"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<ConsumerResponseDto>> listConsumers() {
        var consumers = consumerService.listAllConsumers();
        return ResponseEntity.ok(consumers);
    }
}
