package br.com.apimarketplace.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@Entity
@Table(name = "tb_consumers")
public class Consumer extends User {

    public Consumer(UUID id, String username, String password, String email) {
        super(id, username, password, email);
    }
}
