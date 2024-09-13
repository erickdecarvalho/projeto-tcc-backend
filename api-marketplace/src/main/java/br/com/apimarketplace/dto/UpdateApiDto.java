package br.com.apimarketplace.dto;

public record UpdateApiDto(Integer categoryId, String name, String description, Double price, String providerId) {
}
