package br.com.apimarketplace.repository;

import br.com.apimarketplace.model.Api;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ApiRepository extends JpaRepository<Api, UUID> {
    List<Api> findAllByProviderId(UUID providerId);
}
