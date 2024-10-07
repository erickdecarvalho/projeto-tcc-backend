package br.com.apimarketplace.service;

import br.com.apimarketplace.dto.ConsumerDto;
import br.com.apimarketplace.dto.ProviderDto;
import br.com.apimarketplace.dto.SignupConsumerRequestDto;
import br.com.apimarketplace.dto.SignupProviderRequestDto;
import br.com.apimarketplace.enums.UserRole;
import br.com.apimarketplace.model.Consumer;
import br.com.apimarketplace.model.Provider;
import br.com.apimarketplace.model.User;
import br.com.apimarketplace.repository.ConsumerRepository;
import br.com.apimarketplace.repository.ProviderRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Service
public class AuthService {

    private final ConsumerRepository consumerRepository;

    private final ProviderRepository providerRepository;

    public AuthService(ConsumerRepository consumerRepository, ProviderRepository providerRepository) {
        this.consumerRepository = consumerRepository;
        this.providerRepository = providerRepository;
    }

    public ConsumerDto signupConsumer(SignupConsumerRequestDto signupConsumerRequestDto) {
        Consumer consumer = new Consumer();
        List<UserRole> userRoleList = new ArrayList<>();

        if (consumerRepository.existsByEmail(signupConsumerRequestDto.email())){
            throw new ResponseStatusException(HttpStatus.CONFLICT,"Email already in use");
        }
        userRoleList.add(UserRole.GET);

        consumer.setUsername(signupConsumerRequestDto.username());
        consumer.setPassword(new BCryptPasswordEncoder().encode(signupConsumerRequestDto.password()));
        consumer.setEmail(signupConsumerRequestDto.email());

        consumer.setRole(userRoleList);

        return consumerRepository.save(consumer).getDto();
    }

    public ProviderDto signupProvider(SignupProviderRequestDto signupProviderRequestDto) {
        Provider provider = new Provider();
        List<UserRole> userRoleList = new ArrayList<>();
        if (providerRepository.existsByEmail(signupProviderRequestDto.email())){
            throw new ResponseStatusException(HttpStatus.CONFLICT,"Email already in use");
        }
        userRoleList.add(UserRole.POST);
        userRoleList.add(UserRole.DELETE);
        userRoleList.add(UserRole.GET);

        provider.setUsername(signupProviderRequestDto.username());
        provider.setPassword(new BCryptPasswordEncoder().encode(signupProviderRequestDto.password()));
        provider.setEmail(signupProviderRequestDto.email());
        provider.setOrganizationName(signupProviderRequestDto.organizationName());

        provider.setRole(userRoleList);

        return providerRepository.save(provider).getDto();
    }
}
