package br.com.apimarketplace.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record EmailSenderDto(
        @NotBlank
        @Email
        String userEmail
) {
}
