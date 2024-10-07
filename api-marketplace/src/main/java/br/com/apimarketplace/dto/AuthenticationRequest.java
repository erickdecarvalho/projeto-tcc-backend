package br.com.apimarketplace.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

public record AuthenticationRequest(
       @NotBlank String email,
       @NotBlank String password
) {
}
