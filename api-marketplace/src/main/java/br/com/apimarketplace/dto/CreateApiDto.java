package br.com.apimarketplace.dto;

import java.util.UUID;

public record CreateApiDto(Integer categoryId, String name, String description, Double price, UUID providerId) {
}
