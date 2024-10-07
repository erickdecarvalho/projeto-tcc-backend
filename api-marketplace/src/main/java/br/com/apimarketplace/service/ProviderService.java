package br.com.apimarketplace.service;

import br.com.apimarketplace.dto.CreateProviderDto;
import br.com.apimarketplace.dto.ProviderResponseDto;
import br.com.apimarketplace.enums.UserRole;
import br.com.apimarketplace.model.Provider;
import br.com.apimarketplace.repository.ProviderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProviderService {

    private ProviderRepository providerRepository;

    public ProviderService(ProviderRepository providerRepository) {
        this.providerRepository = providerRepository;
    }

    @Transactional
    public UUID createProvider(CreateProviderDto createProviderDto) {

        List<UserRole> userRoleList = new ArrayList<>();
        userRoleList.add(UserRole.POST);
        userRoleList.add(UserRole.DELETE);
        userRoleList.add(UserRole.GET);

        var provider = new Provider(UUID.randomUUID(),
                createProviderDto.username(),
                createProviderDto.password(),
                createProviderDto.email(),
                userRoleList,
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
}
