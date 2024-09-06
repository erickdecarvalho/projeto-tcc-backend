package br.com.apimarketplace.repository;

import br.com.apimarketplace.model.Api;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ApiRepository extends JpaRepository<Api, UUID> {
}
