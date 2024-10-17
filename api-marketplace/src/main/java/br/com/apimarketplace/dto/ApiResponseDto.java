package br.com.apimarketplace.dto;

import br.com.apimarketplace.model.Api;
import br.com.apimarketplace.model.Endpoint;
import br.com.apimarketplace.model.Parameter;
import br.com.apimarketplace.model.Provider;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public record ApiResponseDto(
        UUID id,
        Long category,
        String name,
        String description,
        Double price,
        String base_url,
        String converted_url,
        ProviderDto providerId,
        List<EndpointDto> endpoints) {

    // Método de conversão de Api para ApiResponseDto
    public static ApiResponseDto fromApi(Api api) {
        return new ApiResponseDto(
                api.getId(),
                api.getApiCategory().getId(),
                api.getName(),
                api.getDescription(),
                api.getPrice(),
                api.getBase_url(),
                api.getConverted_url(),
                ProviderDto.fromProvider(api.getProvider()), // Conversão do Provider para ProviderDto
                convertToEndpointDtoList(api.getEndpoints())
        );
    }

    // Conversão de Endpoints
    private static List<EndpointDto> convertToEndpointDtoList(List<Endpoint> endpoints) {
        return endpoints.stream()
                .map(endpoint -> new EndpointDto(
                        endpoint.getId(),
                        endpoint.getName(),
                        endpoint.getUrl(),
                        endpoint.getType(),
                        endpoint.getDescription(),
                        convertToParamsDtoList(endpoint.getParameters())))
                .collect(Collectors.toList());
    }

    // Conversão de Parameters
    private static List<ParameterDto> convertToParamsDtoList(List<Parameter> params) {
        return params.stream()
                .map(param -> new ParameterDto(
                        param.getId(),
                        param.getName(),
                        param.getType(),
                        param.isOptional(),
                        param.getDescription()))
                .collect(Collectors.toList());
    }
}
