package br.com.apimarketplace.model;

import br.com.apimarketplace.enums.UserRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
@Setter
@Table(name = "tb_providers")
public class Provider extends User{

    @Column(name = "organization_name")
    private String organizationName;

    @OneToMany(mappedBy = "provider", cascade = CascadeType.ALL)
    private List<Api> apis;

    public Provider(UUID id, String username, String password, String email, UserRole role, String organizationName) {
        super(id, username, password, email, role);
        this.organizationName = organizationName;
    }
}
