package br.com.apimarketplace.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record UpdateProviderDto(
        UUID providerId,
        String username,
        @Email
        String email,
        String organizationName) {
}
