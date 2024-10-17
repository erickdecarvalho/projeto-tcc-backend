package br.com.apimarketplace.service;

import br.com.apimarketplace.dto.*;
import br.com.apimarketplace.model.Api;
import br.com.apimarketplace.model.Endpoint;
import br.com.apimarketplace.model.Parameter;
import br.com.apimarketplace.repository.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
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

    public void updateEndpointById(UUID endpointId, EndpointDto endpointDto) {
        var endpointEntity = endpointRepository.findById(endpointId);

        if (endpointEntity.isPresent()) {
            var endpoint = endpointEntity.get();

            // Atualizar os dados básicos do endpoint
            if (endpointDto.name() != null) {
                endpoint.setName(endpointDto.name());
            }

            if (endpointDto.url() != null) {
                endpoint.setUrl(endpointDto.url());
            }

            if (endpointDto.type() != null) {
                endpoint.setType(endpointDto.type());
            }

            if (endpointDto.description() != null) {
                endpoint.setDescription(endpointDto.description());
            }

            // Atualizar parâmetros
            if (endpointDto.params() != null) {
                List<Parameter> existingParams = endpoint.getParameters();  // Parâmetros existentes
                List<ParameterDto> updatedParamsDto = endpointDto.params();  // Novos parâmetros

                // Mapear parâmetros existentes pelo ID ou nome (dependendo do que você considerar único)
                Map<UUID, Parameter> existingParamsMap = existingParams.stream()
                        .collect(Collectors.toMap(Parameter::getId, param -> param));  // Mapeia pelo ID, se disponível

                // Atualizar ou adicionar novos parâmetros
                for (ParameterDto paramDto : updatedParamsDto) {
                    UUID paramId = paramDto.id();  // Supondo que o DTO tem o campo "id"

                    if (existingParamsMap.containsKey(paramId)) {
                        // Atualizar o parâmetro existente
                        Parameter parameter = existingParamsMap.get(paramId);

                        if (paramDto.name() != null) {
                            parameter.setName(paramDto.name());
                        }

                        if (paramDto.type() != null) {
                            parameter.setType(paramDto.type());
                        }

                        if (paramDto.description() != null) {
                            parameter.setDescription(paramDto.description());
                        }

                        parameter.setOptional(paramDto.optional());

                        // Remover do mapa, pois já foi tratado
                        existingParamsMap.remove(paramId);
                    } else {
                        // Criar um novo parâmetro se ele não existir
                        Parameter newParam = new Parameter();
                        newParam.setId(paramDto.id());  // Supondo que você usa UUID nos parâmetros também
                        newParam.setName(paramDto.name());
                        newParam.setType(paramDto.type());
                        newParam.setOptional(paramDto.optional());
                        newParam.setDescription(paramDto.description());
                        newParam.setEndpoint(endpoint);

                        endpoint.getParameters().add(newParam);  // Adiciona à lista de parâmetros do endpoint
                    }
                }

                // Remover parâmetros que não estão mais na nova lista
                existingParams.removeIf(param -> existingParamsMap.containsKey(param.getId()));
            }

            // Salvar o endpoint com as atualizações
            endpointRepository.save(endpoint);
        } else {
            throw new EntityNotFoundException("Endpoint não encontrado");
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
                        param.getId(),
                        param.getName(),
                        param.getType(),
                        param.isOptional(),
                        param.getDescription()))
                .collect(Collectors.toList());
    }

    public void deleteParameterById(UUID parameterId) {
        var parameterExists = parameterRepository.existsById(parameterId);

        if (parameterExists) {
            parameterRepository.deleteById(parameterId);
        }
    }

    public void deleteEndpointById(UUID endpointId) {
        var endpointExists = endpointRepository.existsById(endpointId);

        if (endpointExists) {
            endpointRepository.deleteById(endpointId);
        }
    }
}
