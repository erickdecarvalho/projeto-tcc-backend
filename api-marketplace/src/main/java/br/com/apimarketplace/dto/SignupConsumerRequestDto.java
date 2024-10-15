package br.com.apimarketplace.dto;

import br.com.apimarketplace.validator.ValidPassword;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;


public record SignupConsumerRequestDto(
        @NotBlank
        String username,
        @NotBlank
        @ValidPassword
        String password,
        @NotBlank
        @Email
        String email) {
}
