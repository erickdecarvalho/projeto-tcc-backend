package br.com.apimarketplace.service;

import br.com.apimarketplace.model.Api;
import br.com.apimarketplace.repository.ApiRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ApiService {

    private ApiRepository apiRepository;

    public ApiService(ApiRepository apiRepository) {
        this.apiRepository = apiRepository;
    }

    public void createApi() {
        var api = new Api(UUID.randomUUID(),
                "2",
                "API Football",
                "Uma api sobre futebol",
                29.9,
                "1"
                );

        apiRepository.save(api);
    }
}
