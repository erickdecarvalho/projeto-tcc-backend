package br.com.apimarketplace.controller;

import br.com.apimarketplace.dto.ConsumerDto;
import br.com.apimarketplace.dto.SignupConsumerRequestDto;
import br.com.apimarketplace.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthenticationController {

    private AuthService authService;

    public AuthenticationController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/clientes/registrar")
    public ResponseEntity<?> signupConsumer(@RequestBody SignupConsumerRequestDto signupConsumerRequestDto) {
        ConsumerDto createdConsumer = authService.signupConsumer(signupConsumerRequestDto);

        return new ResponseEntity<>(createdConsumer, HttpStatus.OK);
    }
}
