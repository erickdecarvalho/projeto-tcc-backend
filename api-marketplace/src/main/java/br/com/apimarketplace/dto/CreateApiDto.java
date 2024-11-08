package br.com.apimarketplace.dto;

import java.util.UUID;

public record CreateApiDto(Integer categoryId, String name, String description, Double price, String base_url, String requiredPlan, UUID providerId) {
}
