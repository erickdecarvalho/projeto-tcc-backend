package br.com.apimarketplace.service;

import br.com.apimarketplace.dto.*;
import br.com.apimarketplace.enums.UserRole;
import br.com.apimarketplace.model.Api;
import br.com.apimarketplace.model.Provider;
import br.com.apimarketplace.repository.ApiCategoryRepository;
import br.com.apimarketplace.repository.ApiRepository;
import br.com.apimarketplace.repository.ProviderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProviderService {

    private ProviderRepository providerRepository;
    private ApiRepository apiRepository;
    private ApiCategoryRepository apiCategoryRepository;

    public ProviderService(ProviderRepository providerRepository, ApiRepository apiRepository, ApiCategoryRepository apiCategoryRepository) {
        this.providerRepository = providerRepository;
        this.apiRepository = apiRepository;
        this.apiCategoryRepository = apiCategoryRepository;
    }

    @Transactional
    public UUID createProvider(CreateProviderDto createProviderDto) {

        var provider = new Provider(UUID.randomUUID(),
                createProviderDto.username(),
                createProviderDto.password(),
                createProviderDto.email(),
                UserRole.PROVIDER,
                createProviderDto.organizationName()
        );

        var savedProvider = providerRepository.save(provider);
        return savedProvider.getId();
    }

    public Optional<ProviderResponseDto> getProviderById(String providerId) {
        return providerRepository.findById(UUID.fromString(providerId))
                .map(provider -> new ProviderResponseDto(provider.getId(), provider.getUsername(),
                        provider.getPassword(), provider.getEmail(), provider.getOrganizationName())
                );
    }

    public List<ProviderResponseDto> listAllProviders() {
        return providerRepository.findAll().stream()
                .map(provider -> new ProviderResponseDto(provider.getId(), provider.getUsername(),
                        provider.getPassword(), provider.getEmail(), provider.getOrganizationName()))
                .collect(Collectors.toList());
    }

    public boolean postApi(UUID providerId, CreateApiDto createApiDto) throws IOException {
        Optional<Provider> optionalProvider = providerRepository.findById(providerId);
        if (optionalProvider.isPresent()) {
            Api api = new Api();
            api.setApiCategory(apiCategoryRepository.getReferenceById(createApiDto.categoryId()));
            api.setName(createApiDto.name());
            api.setDescription(createApiDto.description());
            api.setBase_url(createApiDto.base_url());
            api.setConverted_url("www.meusite.com");
            api.setProvider(optionalProvider.get());

            apiRepository.save(api);
            return true;
        }

        return false;
    }

    @Transactional
    public List<ApiDto> getAllApis(UUID providerId) {
        List<Api> apis = apiRepository.findAllByProviderId(providerId);
        System.out.println(apis);
        return apis.stream().map(Api::getApiDto).collect(Collectors.toList());
    }

    public ApiDto getApiById(UUID apiId) {
        Optional<Api> optionalApi = apiRepository.findById(apiId);
        System.out.println(optionalApi);
        if(optionalApi.isPresent()) {
            return optionalApi.get().getApiDto();
        }

        return null;
    }
}
