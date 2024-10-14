package br.com.apimarketplace.dto;

import java.util.UUID;

public record ApiResponseDto(
        UUID id,
        Long category,
        String name,
        String description,
        Double price,
        UUID providerId)
{}
