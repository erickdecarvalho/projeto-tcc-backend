package br.com.apimarketplace.dto;

import br.com.apimarketplace.validator.ValidPassword;
import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record UpdatePasswordInsiderDto(
        UUID userId,
        @NotBlank
        String oldPassword,
        @NotBlank
        @ValidPassword
        String newestPassword,
        @NotBlank
        @ValidPassword
        String newestPasswordConfirm
) {
}
