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
                        param.getId(),
                        param.getName(),
                        param.getType(),
                        param.isOptional(),
                        param.getDescription()))
                .collect(Collectors.toList());
    }

    // Método de conversão de ParameterDto para Parameter
    public static List<Parameter> convertToParamsEntityList(List<ParameterDto> paramDtos) {
        return paramDtos.stream()
                .map(paramDto -> new Parameter(
                        paramDto.name(),
                        paramDto.type(),
                        paramDto.optional(),
                        paramDto.description()))
                .collect(Collectors.toList());
    }
}
