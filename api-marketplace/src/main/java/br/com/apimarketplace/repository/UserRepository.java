package br.com.apimarketplace.repository;

import br.com.apimarketplace.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    User findFirstByEmail(String email);
}
