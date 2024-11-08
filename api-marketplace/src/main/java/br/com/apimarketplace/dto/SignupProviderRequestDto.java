package br.com.apimarketplace.dto;

import br.com.apimarketplace.validator.ValidPassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;



public record SignupProviderRequestDto(
        @NotBlank
        String username,
        @NotBlank
        @ValidPassword
        String password,
        @Email
        @NotBlank
        String email,
        String organizationName) {
}
