package br.com.apimarketplace.dto;

import br.com.apimarketplace.enums.ParameterType;

public record ParameterDto(
        String name,
        ParameterType type,
        boolean optional,
        String description
) {}
