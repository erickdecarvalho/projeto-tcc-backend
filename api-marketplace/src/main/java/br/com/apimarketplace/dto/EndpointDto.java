package br.com.apimarketplace.dto;

import br.com.apimarketplace.enums.EndpointType;
import br.com.apimarketplace.model.Parameter;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public record EndpointDto(
        UUID id,
        String name,
        String url,
        EndpointType type,
        String description,
        List<ParameterDto> params
) {
    // Método de conversão de Parameter para ParameterDto
    public static List<ParameterDto> convertToParamsDtoList(List<Parameter> params) {
        return params.stream()
                .map(param -> new ParameterDto(
                        param.getName(),
                        param.getType(),
                        param.isOptional(),
                        param.getDescription()))
                .collect(Collectors.toList());
    }
}
