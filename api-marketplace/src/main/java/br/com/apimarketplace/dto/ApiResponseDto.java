package br.com.apimarketplace.dto;

import java.util.UUID;

public record ApiResponseDto(
        UUID id,
        String category,
        String name,
        String description,
        Double price)
{}
