package br.com.apimarketplace.model;

import br.com.apimarketplace.dto.ConsumerDto;
import br.com.apimarketplace.enums.UserRole;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@Entity
@Table(name = "tb_consumers")
public class Consumer extends User {

    public Consumer(UUID id, String username, String password, String email, UserRole role) {
        super(id, username, password, email, role);
    }

    public ConsumerDto getDto() {
        ConsumerDto consumerDto = new ConsumerDto();
        consumerDto.setId(getId());
        consumerDto.setUsername(getUsername());
        consumerDto.setPassword(getPassword());
        consumerDto.setEmail(getEmail());
        consumerDto.setRole(getRole());

        return consumerDto;
    }
}
