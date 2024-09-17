package br.com.apimarketplace.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    public Provider(UUID id, String username, String password, String email, String organizationName) {
        super(id, username, password, email);
        this.organizationName = organizationName;
    }
}
