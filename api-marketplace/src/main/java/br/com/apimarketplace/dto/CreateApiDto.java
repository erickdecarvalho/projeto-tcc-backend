package br.com.apimarketplace.dto;

public record CreateApiDto(String categoryId, String name, String description, Double price, String providerId) {
}
