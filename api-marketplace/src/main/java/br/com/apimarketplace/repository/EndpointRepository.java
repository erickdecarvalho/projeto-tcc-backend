package br.com.apimarketplace.repository;

import br.com.apimarketplace.model.Endpoint;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface EndpointRepository extends JpaRepository<Endpoint, UUID> {
    List<Endpoint> findByApiId(UUID apiId);
}
