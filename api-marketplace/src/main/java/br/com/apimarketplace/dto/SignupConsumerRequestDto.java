package br.com.apimarketplace.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record SignupConsumerRequestDto(
        @NotBlank String username,
        @NotBlank String password,
        @NotBlank @Valid String email) {
}
