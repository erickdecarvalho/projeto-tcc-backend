package br.com.apimarketplace.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record SignupProviderRequestDto(
        @NotBlank String username,
        @NotBlank String password,
        @NotBlank String email,
        String organizationName) {
}
