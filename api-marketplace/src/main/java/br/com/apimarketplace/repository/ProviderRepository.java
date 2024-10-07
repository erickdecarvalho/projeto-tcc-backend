package br.com.apimarketplace.repository;

import br.com.apimarketplace.model.Provider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProviderRepository extends JpaRepository<Provider, UUID> {
    boolean existsByEmail(String email);
}
