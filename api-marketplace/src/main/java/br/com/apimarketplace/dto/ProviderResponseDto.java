package br.com.apimarketplace.dto;

import java.util.UUID;

public record ProviderResponseDto(
        UUID id,
        String username, String password, String email, String organizationName) {
}
