package br.com.apimarketplace.dto;

import br.com.apimarketplace.enums.UserRole;
import br.com.apimarketplace.model.Provider;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
public class ProviderDto {
    private UUID id;
    private String username;
    private String password;
    private String email;
    private String organizationName;
    private UserRole role;

    public ProviderDto(UUID id, String username, String email, String organizationName) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.organizationName = organizationName;
    }

    // Método para converter Provider para ProviderDto
    public static ProviderDto fromProvider(Provider provider) {
        return new ProviderDto(
                provider.getId(),
                provider.getUsername(),
                provider.getEmail(),
                provider.getOrganizationName()
        );
    }
}


