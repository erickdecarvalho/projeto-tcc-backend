package br.com.apimarketplace.controller;

import br.com.apimarketplace.dto.ConsumerResponseDto;
import br.com.apimarketplace.dto.CreateConsumerDto;
import br.com.apimarketplace.model.Consumer;
import br.com.apimarketplace.service.ConsumerService;
import org.springframework.http.ResponseEntity;
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

    @PostMapping
    public ResponseEntity<Consumer> createConsumer(@RequestBody CreateConsumerDto createConsumerDto) {
        var consumerId = consumerService.createConsumer(createConsumerDto);
        return ResponseEntity.created(URI.create("/consumidores/" + consumerId.toString())).build();
    }

    @GetMapping("/{consumerId}")
    public ResponseEntity<ConsumerResponseDto> getConsumerById(@PathVariable("consumerId") String consumerId) {
        var consumer = consumerService.getConsumerById(consumerId);

        if (consumer.isPresent()) {
            return ResponseEntity.ok(consumer.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<ConsumerResponseDto>> listConsumers() {
        var consumers = consumerService.listAllConsumers();

        return ResponseEntity.ok(consumers);
    }
}
