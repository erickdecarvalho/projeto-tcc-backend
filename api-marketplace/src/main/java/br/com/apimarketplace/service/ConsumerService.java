package br.com.apimarketplace.service;

import br.com.apimarketplace.dto.ConsumerResponseDto;
import br.com.apimarketplace.dto.CreateConsumerDto;
import br.com.apimarketplace.dto.ProviderResponseDto;
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

    private ConsumerRepository consumerRepository;

    public ConsumerService(ConsumerRepository consumerRepository) {
        this.consumerRepository = consumerRepository;
    }

    @Transactional
    public UUID createConsumer(CreateConsumerDto createConsumerDto) {

        var consumer = new Consumer(UUID.randomUUID(),
                createConsumerDto.username(),
                new BCryptPasswordEncoder().encode(createConsumerDto.password()),
                createConsumerDto.email(),
                UserRole.CONSUMER
        );

        var savedProvider = consumerRepository.save(consumer);
        return savedProvider.getId();
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
