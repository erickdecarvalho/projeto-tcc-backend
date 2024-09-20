package br.com.apimarketplace.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tb_apis")
public class Api {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "api_category_id")
    private ApiCategory apiCategory;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "price")
    private Double price;

    @ManyToOne
    @JoinColumn(name = "provider_id")
    private Provider provider;

    private List<String> endpoints;

    public Api(UUID id, ApiCategory apiCategory, String name, String description, Double price, Provider provider) {
        this.id = id;
        this.apiCategory = apiCategory;
        this.name = name;
        this.description = description;
        this.price = price;
        this.provider = provider;
    }

}

