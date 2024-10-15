package br.com.apimarketplace.service;

import br.com.apimarketplace.dto.ConsumerResponseDto;
import br.com.apimarketplace.dto.UpdateConsumerDto;
import br.com.apimarketplace.enums.UserRole;
import br.com.apimarketplace.model.Consumer;
import br.com.apimarketplace.repository.ConsumerRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ConsumerService {

    private final ConsumerRepository consumerRepository;

    public ConsumerService(ConsumerRepository consumerRepository) {
        this.consumerRepository = consumerRepository;
    }

    @Transactional
    public UUID updateConsumer(UpdateConsumerDto updateConsumerDto) {
        Optional<Consumer> consumerOptional = consumerRepository.findById(updateConsumerDto.consumerId());
        if (consumerOptional.isEmpty()){
            throw new RuntimeException("Consumer not found by id "+ updateConsumerDto.consumerId());
        }

        Consumer consumer = consumerOptional.get();

        if (!consumer.getUsername().equals(updateConsumerDto.username())){
            consumer.setUsername(updateConsumerDto.username());
        }
        if (!consumer.getEmail().equals(updateConsumerDto.email())) {
            consumer.setEmail(updateConsumerDto.email());
        }

        return consumerRepository.save(consumer).getId();
    }

    public Optional<ConsumerResponseDto> getConsumerById(String providerId) {
        return consumerRepository.findById(UUID.fromString(providerId))
                .map(provider -> new ConsumerResponseDto(provider.getId(), provider.getUsername(),
                        provider.getPassword(), provider.getEmail())
                );
    }

    public List<ConsumerResponseDto> listAllConsumers() {
        return consumerRepository.findAll().stream()
                .map(provider -> new ConsumerResponseDto(provider.getId(), provider.getUsername(),
                        provider.getPassword(), provider.getEmail()))
                .collect(Collectors.toList());
    }


}
