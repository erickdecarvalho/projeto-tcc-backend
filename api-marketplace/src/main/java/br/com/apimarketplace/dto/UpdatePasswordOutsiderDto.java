package br.com.apimarketplace.dto;

import br.com.apimarketplace.validator.ValidPassword;
import jakarta.validation.constraints.NotBlank;

public record UpdatePasswordOutsiderDto(
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
