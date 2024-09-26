package br.com.apimarketplace.dto;

import br.com.apimarketplace.enums.UserRole;
import lombok.Data;

import java.util.UUID;

@Data
public class ConsumerDto {
    private UUID id;
    private String username;
    private String password;
    private String email;
    private UserRole role;
}
