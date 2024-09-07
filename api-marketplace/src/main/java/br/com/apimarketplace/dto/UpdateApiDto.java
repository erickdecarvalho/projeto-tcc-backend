package br.com.apimarketplace.dto;

public record UpdateApiDto(String categoryId, String name, String description, Double price, String providerId) {
}
