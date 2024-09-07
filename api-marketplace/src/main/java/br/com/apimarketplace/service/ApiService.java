package br.com.apimarketplace.service;

import br.com.apimarketplace.dto.CreateApiDto;
import br.com.apimarketplace.dto.UpdateApiDto;
import br.com.apimarketplace.model.Api;
import br.com.apimarketplace.repository.ApiRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ApiService {

    private ApiRepository apiRepository;

    public ApiService(ApiRepository apiRepository) {
        this.apiRepository = apiRepository;
    }

    public UUID createApi(CreateApiDto createApiDto) {
        var api = new Api(UUID.randomUUID(),
                createApiDto.categoryId(),
                createApiDto.name(),
                createApiDto.description(),
                createApiDto.price(),
                createApiDto.providerId()
                );

        var savedApi = apiRepository.save(api);
        return savedApi.getId();
    }

    public Optional<Api> getApiById(String apiId) {
        return apiRepository.findById(UUID.fromString(apiId));
    }

    public List<Api> listApis() {
        return apiRepository.findAll();
    }

    public void updateApiById(String userId, UpdateApiDto updateApiDto) {
        var id = UUID.fromString(userId);

        var apiEntity = apiRepository.findById(id);

        if (apiEntity.isPresent()) {
            var api = apiEntity.get();

            if (updateApiDto.categoryId() != null) {
                api.setCategoryId(updateApiDto.categoryId());
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

            if (updateApiDto.providerId() != null) {
                api.setProviderId(updateApiDto.providerId());
            }

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
