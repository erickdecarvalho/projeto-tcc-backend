package br.com.apimarketplace.controller;

import br.com.apimarketplace.dto.CreateProviderDto;
import br.com.apimarketplace.dto.ProviderResponseDto;
import br.com.apimarketplace.model.Provider;
import br.com.apimarketplace.service.ProviderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/provedores")
public class ProviderController {

    private ProviderService providerService;

    public ProviderController(ProviderService providerService) {
        this.providerService = providerService;
    }

    @PostMapping
    public ResponseEntity<Provider> createProvider(@RequestBody CreateProviderDto createProviderDto) {
        var providerId = providerService.createProvider(createProviderDto);
        return ResponseEntity.created(URI.create("/provedores/" + providerId.toString())).build();
    }

    @GetMapping("/{providerId}")
    public ResponseEntity<ProviderResponseDto> getProviderById(@PathVariable("providerId") String providerId) {
        var provider = providerService.getProviderById(providerId);

        if (provider.isPresent()) {
            return ResponseEntity.ok(provider.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<ProviderResponseDto>> listProviders() {
        var providers = providerService.listAllProviders();

        return ResponseEntity.ok(providers);
    }
}
