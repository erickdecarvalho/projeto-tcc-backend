package br.com.apimarketplace.dto;

public record AuthenticationRequest(
        String email,
        String password
) {
}
