package br.com.apimarketplace.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record UpdateConsumerDto(
        UUID consumerId,
        String username,
        @Email
        String email) {
}
