package br.com.apimarketplace.dto;

public record CreateApiDto(Integer categoryId, String name, String description, Double price, String providerId) {
}
