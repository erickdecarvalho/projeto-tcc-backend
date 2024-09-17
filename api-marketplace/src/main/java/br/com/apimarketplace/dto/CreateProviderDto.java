package br.com.apimarketplace.dto;

public record CreateProviderDto(String username, String password, String email, String organizationName) {
}
