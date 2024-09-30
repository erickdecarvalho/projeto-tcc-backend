package br.com.apimarketplace.service;

import br.com.apimarketplace.dto.ConsumerDto;
import br.com.apimarketplace.dto.ProviderDto;
import br.com.apimarketplace.dto.SignupConsumerRequestDto;
import br.com.apimarketplace.dto.SignupProviderRequestDto;
import br.com.apimarketplace.enums.UserRole;
import br.com.apimarketplace.model.Consumer;
import br.com.apimarketplace.model.Provider;
import br.com.apimarketplace.repository.ConsumerRepository;
import br.com.apimarketplace.repository.ProviderRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private ConsumerRepository consumerRepository;

    private ProviderRepository providerRepository;

    public AuthService(ConsumerRepository consumerRepository, ProviderRepository providerRepository) {
        this.consumerRepository = consumerRepository;
        this.providerRepository = providerRepository;
    }

    public ConsumerDto signupConsumer(SignupConsumerRequestDto signupConsumerRequestDto) {
        Consumer consumer = new Consumer();

        consumer.setUsername(signupConsumerRequestDto.username());
        consumer.setPassword(new BCryptPasswordEncoder().encode(signupConsumerRequestDto.password()));
        consumer.setEmail(signupConsumerRequestDto.email());

        consumer.setRole(UserRole.CONSUMER);

        return consumerRepository.save(consumer).getDto();
    }

    public ProviderDto signupProvider(SignupProviderRequestDto signupProviderRequestDto) {
        Provider provider = new Provider();

        provider.setUsername(signupProviderRequestDto.username());
        provider.setPassword(new BCryptPasswordEncoder().encode(signupProviderRequestDto.password()));
        provider.setEmail(signupProviderRequestDto.email());
        provider.setOrganizationName(signupProviderRequestDto.organizationName());

        provider.setRole(UserRole.PROVIDER);

        return providerRepository.save(provider).getDto();
    }
}
