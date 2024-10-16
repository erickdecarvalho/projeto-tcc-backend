package br.com.apimarketplace.service;

import br.com.apimarketplace.dto.*;
import br.com.apimarketplace.model.Api;
import br.com.apimarketplace.model.Endpoint;
import br.com.apimarketplace.model.Parameter;
import br.com.apimarketplace.repository.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ApiService {

    private ApiRepository apiRepository;
    private ApiCategoryRepository apiCategoryRepository;
    private ProviderRepository providerRepository;
    private EndpointRepository endpointRepository;
    private ParameterRepository parameterRepository;

    public ApiService(ApiRepository apiRepository, ApiCategoryRepository apiCategoryRepository, ProviderRepository providerRepository, EndpointRepository endpointRepository, ParameterRepository parameterRepository) {
        this.apiRepository = apiRepository;
        this.apiCategoryRepository = apiCategoryRepository;
        this.providerRepository = providerRepository;
        this.endpointRepository = endpointRepository;
        this.parameterRepository = parameterRepository;
    }

    public UUID createApi(CreateApiDto createApiDto) {
        var apiCategory = apiCategoryRepository.getReferenceById(createApiDto.categoryId());
        var providerId = providerRepository.getReferenceById(createApiDto.providerId());

        var api = new Api(UUID.randomUUID(),
                apiCategory,
                createApiDto.name(),
                createApiDto.description(),
                createApiDto.price(),
                createApiDto.base_url(),
                "www.meusite.com",
                providerId
                );

        var savedApi = apiRepository.save(api);
        return savedApi.getId();
    }

    public Optional<ApiResponseDto> getApiById(String apiId) {
        return apiRepository.findById(UUID.fromString(apiId))
                .map(ApiResponseDto::fromApi);
    }

    public List<ApiResponseDto> listAllApis() {
        return apiRepository.findAll().stream()
                .map(ApiResponseDto::fromApi)
                .collect(Collectors.toList());
    }

    public void updateApiById(String userId, UpdateApiDto updateApiDto) {
        var id = UUID.fromString(userId);

        var apiCategory = apiCategoryRepository.getReferenceById(updateApiDto.categoryId());
        var providerId = providerRepository.getReferenceById(updateApiDto.providerId());
        var apiEntity = apiRepository.findById(id);

        if (apiEntity.isPresent()) {
            var api = apiEntity.get();

            if (updateApiDto.categoryId() != null) {
                api.setApiCategory(apiCategory);
            }

            if (updateApiDto.name() != null) {
                api.setName(updateApiDto.name());
            }

            if (updateApiDto.description() != null) {
                api.setDescription(updateApiDto.description());
            }

            if (updateApiDto.price() != null) {
                api.setPrice(updateApiDto.price());
            }

//            if (updateApiDto.providerId() != null) {
//                api.setProviderId(providerId);
//            }

            apiRepository.save(api);
        }
    }

    public void deleteById(String apiId) {
        var id = UUID.fromString(apiId);

        var apiExists = apiRepository.existsById(id);

        if (apiExists) {
            apiRepository.deleteById(id);
        }
    }

    // Método para criar um novo endpoint e associá-lo à API
    public Endpoint createEndpoint(UUID apiId, EndpointDto endpointDto) {
        Api api = apiRepository.findById(apiId)
                .orElseThrow(() -> new IllegalArgumentException("API not found"));

        Endpoint endpoint = new Endpoint();
        endpoint.setName(endpointDto.name());
        endpoint.setUrl(endpointDto.url());
        endpoint.setType(endpointDto.type());
        endpoint.setDescription(endpointDto.description());
        endpoint.setApi(api);  // Associa o endpoint à API encontrada

        return endpointRepository.save(endpoint);
    }

    public List<EndpointDto> getEndpointsByApi(UUID apiId) {
        List<Endpoint> endpoints = endpointRepository.findByApiId(apiId);
        return endpoints.stream()
                .map(endpoint -> new EndpointDto(
                        endpoint.getId(),
                        endpoint.getName(),
                        endpoint.getUrl(),
                        endpoint.getType(),
                        endpoint.getDescription(),
                        EndpointDto.convertToParamsDtoList(endpoint.getParameters())))  // Chamada do método
                .collect(Collectors.toList());
    }

    // Método que adiciona o parâmetro ao endpoint
    public void addParamToEndpoint(UUID endpointId, ParameterDto parameterDto) {
        // Verifica se o endpoint existe
        Endpoint endpoint = endpointRepository.findById(endpointId)
                .orElseThrow(() -> new EntityNotFoundException("Endpoint not found with id: " + endpointId));

        // Cria um novo parâmetro e associa ao endpoint
        Parameter param = new Parameter();
        param.setName(parameterDto.name());
        param.setType(parameterDto.type());
        param.setOptional(parameterDto.optional());
        param.setDescription(parameterDto.description());
        param.setEndpoint(endpoint); // Associa o parâmetro ao endpoint

        // Salva o parâmetro no banco de dados
        parameterRepository.save(param);
    }

    // Método que retorna a lista de parâmetros de um endpoint específico
    public List<ParameterDto> getParametersByEndpoint(UUID endpointId) {
        // Verifica se o endpoint existe
        Endpoint endpoint = endpointRepository.findById(endpointId)
                .orElseThrow(() -> new EntityNotFoundException("Endpoint not found with id: " + endpointId));

        // Converte os parâmetros associados ao endpoint para DTOs e retorna
        return endpoint.getParameters().stream()
                .map(param -> new ParameterDto(
                        param.getName(),
                        param.getType(),
                        param.isOptional(),
                        param.getDescription()))
                .collect(Collectors.toList());
    }
}
