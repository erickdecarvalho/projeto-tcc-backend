package br.com.apimarketplace.repository;

import br.com.apimarketplace.model.Consumer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ConsumerRepository extends JpaRepository<Consumer, UUID> {
    boolean existsByEmail(String email);
}
