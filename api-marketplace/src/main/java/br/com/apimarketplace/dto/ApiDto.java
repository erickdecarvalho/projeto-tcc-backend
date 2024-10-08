package br.com.apimarketplace.dto;

import br.com.apimarketplace.model.ApiCategory;
import br.com.apimarketplace.model.Provider;
import lombok.Data;

import java.util.UUID;

@Data
public class ApiDto {
    private UUID id;
    private Long categoryId;
    private String name;
    private String description;
    private UUID providerId;
}
