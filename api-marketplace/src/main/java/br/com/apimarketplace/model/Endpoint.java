package br.com.apimarketplace.model;

import br.com.apimarketplace.enums.EndpointType;
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
@Table(name = "tb_endpoints")
public class Endpoint {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "name")
    private String name;

    @Column(name = "url")
    private String url;

    @Column(name = "type")
    private EndpointType type;

    @Column(name = "description")
    private String description;

    @ManyToOne
    @JoinColumn(name = "api_id")
    private Api api;

    @OneToMany(mappedBy = "endpoint", cascade = CascadeType.ALL)
    private List<Parameter> parameters;
}
