package br.com.apimarketplace.service;

import br.com.apimarketplace.dto.ApiCategoryResponseDto;
import br.com.apimarketplace.dto.ApiResponseDto;
import br.com.apimarketplace.dto.CreateApiDto;
import br.com.apimarketplace.dto.UpdateApiDto;
import br.com.apimarketplace.model.Api;
import br.com.apimarketplace.repository.ApiCategoryRepository;
import br.com.apimarketplace.repository.ApiRepository;
import br.com.apimarketplace.repository.ProviderRepository;
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

    public ApiService(ApiRepository apiRepository, ApiCategoryRepository apiCategoryRepository, ProviderRepository providerRepository) {
        this.apiRepository = apiRepository;
        this.apiCategoryRepository = apiCategoryRepository;
        this.providerRepository = providerRepository;
    }

    public UUID createApi(CreateApiDto createApiDto) {
        var apiCategory = apiCategoryRepository.getReferenceById(createApiDto.categoryId());
        var providerId = providerRepository.getReferenceById(createApiDto.providerId());

        var api = new Api(UUID.randomUUID(),
                apiCategory,
                createApiDto.name(),
                createApiDto.description(),
                createApiDto.price(),
                providerId
                );

        var savedApi = apiRepository.save(api);
        return savedApi.getId();
    }

    public Optional<ApiResponseDto> getApiById(String apiId) {
        return apiRepository.findById(UUID.fromString(apiId))
                .map(api -> new ApiResponseDto(api.getId(), api.getApiCategory().getId(), api.getName(),
                        api.getDescription(), api.getPrice(), api.getProvider().getId())
                );
    }

    public List<ApiResponseDto> listAllApis() {
        return apiRepository.findAll().stream()
                .map(api -> new ApiResponseDto(api.getId(), api.getApiCategory().getId(), api.getName(),
                        api.getDescription(), api.getPrice(), api.getProvider().getId()))
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
}
