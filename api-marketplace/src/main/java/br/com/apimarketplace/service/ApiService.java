package br.com.apimarketplace.service;

import br.com.apimarketplace.dto.CreateApiDto;
import br.com.apimarketplace.model.Api;
import br.com.apimarketplace.repository.ApiRepository;
import org.springframework.stereotype.Service;

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
}
