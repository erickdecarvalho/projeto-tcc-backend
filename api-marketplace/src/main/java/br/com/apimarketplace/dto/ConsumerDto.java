package br.com.apimarketplace.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class ConsumerDto {
    UUID id;
    String username;
    String password;
    String email;
}
