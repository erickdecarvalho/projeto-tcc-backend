package br.com.apimarketplace.model;

import br.com.apimarketplace.dto.ApiDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
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

    @Column(name = "base_url")
    private String base_url;

    @Column(name = "converted_url")
    private String converted_url;

    @Column(name = "required_plan")
    private String required_plan;

    @ManyToOne
    @JoinColumn(name = "provider_id")
    private Provider provider;

    @OneToMany(mappedBy = "api", cascade = CascadeType.ALL)
    private List<Endpoint> endpoints;

    public Api(UUID id, ApiCategory apiCategory, String name, String description, Double price, String base_url, String converted_url, String required_plan, Provider provider) {
        this.id = id;
        this.apiCategory = apiCategory;
        this.name = name;
        this.description = description;
        this.price = price;
        this.base_url = base_url;
        this.converted_url = converted_url;
        this.required_plan = required_plan;
        this.provider = provider;
        this.endpoints = new ArrayList<>();
    }


    public ApiDto getApiDto() {
        ApiDto apiDto = new ApiDto();
        apiDto.setId(id);
        apiDto.setCategoryId(apiCategory.getId());
        apiDto.setName(name);
        apiDto.setDescription(description);
        apiDto.setRequiredPlan(required_plan);
        apiDto.setProviderId(provider.getId());

        return apiDto;
    }
}

