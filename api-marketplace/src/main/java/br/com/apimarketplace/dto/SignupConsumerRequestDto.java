package br.com.apimarketplace.dto;

import java.util.UUID;

public record SignupConsumerRequestDto(UUID id, String username, String password, String email) {
}
