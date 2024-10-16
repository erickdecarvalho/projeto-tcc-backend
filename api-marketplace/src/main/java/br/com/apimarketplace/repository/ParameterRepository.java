package br.com.apimarketplace.repository;

import br.com.apimarketplace.model.Parameter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ParameterRepository extends JpaRepository<Parameter, UUID> {
}
