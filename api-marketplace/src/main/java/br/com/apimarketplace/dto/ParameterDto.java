package br.com.apimarketplace.dto;

import br.com.apimarketplace.enums.ParameterType;

import java.util.UUID;

public record ParameterDto(
        UUID id,
        String name,
        ParameterType type,
        boolean optional,
        String description
) {}
