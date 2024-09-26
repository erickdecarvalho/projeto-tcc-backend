package br.com.apimarketplace.service;

import br.com.apimarketplace.dto.ConsumerDto;
import br.com.apimarketplace.dto.SignupConsumerRequestDto;
import br.com.apimarketplace.enums.UserRole;
import br.com.apimarketplace.model.Consumer;
import br.com.apimarketplace.repository.ConsumerRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private ConsumerRepository consumerRepository;

    public AuthService(ConsumerRepository consumerRepository) {
        this.consumerRepository = consumerRepository;
    }

    public ConsumerDto signupConsumer(SignupConsumerRequestDto signupConsumerRequestDto) {
        Consumer consumer = new Consumer();

        consumer.setUsername(signupConsumerRequestDto.username());
        consumer.setPassword(new BCryptPasswordEncoder().encode(signupConsumerRequestDto.password()));
        consumer.setEmail(signupConsumerRequestDto.email());

        consumer.setRole(UserRole.CONSUMER);

        return consumerRepository.save(consumer).getDto();
    }
}
