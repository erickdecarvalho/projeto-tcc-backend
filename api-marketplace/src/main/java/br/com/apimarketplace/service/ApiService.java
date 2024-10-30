package br.com.apimarketplace.service;

import br.com.apimarketplace.dto.*;
import br.com.apimarketplace.model.Api;
import br.com.apimarketplace.model.Endpoint;
import br.com.apimarketplace.model.Parameter;
import br.com.apimarketplace.repository.*;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.ssl.TrustAllStrategy;
import org.apache.hc.core5.ssl.TrustStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;

import java.security.KeyStoreException;
import javax.net.ssl.*;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.core5.ssl.SSLContextBuilder;

import org.apache.hc.client5.http.impl.classic.HttpClients; // Esse é o import que faltava
import org.apache.hc.core5.ssl.SSLContextBuilder;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.ssl.SSLContextBuilder;
import org.apache.hc.core5.ssl.TrustStrategy;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.core5.ssl.SSLContextBuilder;
import org.apache.hc.core5.ssl.TrustStrategy;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.security.cert.X509Certificate;
import javax.net.ssl.SSLContext;
import java.security.cert.X509Certificate;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.ssl.SSLContextBuilder;
import org.apache.hc.core5.ssl.TrustStrategy;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.security.cert.X509Certificate;
@Service
public class ApiService {

    @Autowired
    private ApiRepository apiRepository;

    @Autowired
    private ApiCategoryRepository apiCategoryRepository;

    @Autowired
    private ProviderRepository providerRepository;

    @Autowired
    private EndpointRepository endpointRepository;

    @Autowired
    private ParameterRepository parameterRepository;


    public UUID createApi(CreateApiDto createApiDto) {
        var apiCategory = apiCategoryRepository.getReferenceById(createApiDto.categoryId());
        var providerId = providerRepository.getReferenceById(createApiDto.providerId());

        var api = new Api(UUID.randomUUID(),
                apiCategory,
                createApiDto.name(),
                createApiDto.description(),
                createApiDto.price(),
                createApiDto.base_url(),
                "www.meusite.com",
                createApiDto.requiredPlan(),
                providerId
        );

        var savedApi = apiRepository.save(api);
        return savedApi.getId();
    }

    public Optional<ApiResponseDto> getApiById(String apiId) {
        return apiRepository.findById(UUID.fromString(apiId))
                .map(ApiResponseDto::fromApi);
    }

    public List<ApiResponseDto> listAllApis() {
        return apiRepository.findAll().stream()
                .map(ApiResponseDto::fromApi)
                .collect(Collectors.toList());
    }

    public void updateApiById(String userId, UpdateApiDto updateApiDto) {
        var id = UUID.fromString(userId);

        var apiCategory = apiCategoryRepository.getReferenceById(updateApiDto.categoryId());
        var providerId = providerRepository.getReferenceById(updateApiDto.providerId());
        var apiEntity = apiRepository.findById(id);

        if (apiEntity.isPresent()) {
            var api = apiEntity.get();

            if (updateApiDto.categoryId() != null) {
                api.setApiCategory(apiCategory);
            }

            if (updateApiDto.name() != null) {
                api.setName(updateApiDto.name());
            }

            if (updateApiDto.description() != null) {
                api.setDescription(updateApiDto.description());
            }

            if (updateApiDto.price() != null) {
                api.setPrice(updateApiDto.price());
            }

//            if (updateApiDto.providerId() != null) {
//                api.setProviderId(providerId);
//            }

            apiRepository.save(api);
        }
    }

    public void deleteById(String apiId) {
        var id = UUID.fromString(apiId);

        var apiExists = apiRepository.existsById(id);

        if (apiExists) {
            apiRepository.deleteById(id);
        }
    }

    public Api findById(String apiId) {
        Api api = apiRepository.findById(UUID.fromString(apiId))
                .orElseThrow(() -> new IllegalArgumentException("API not found"));

        return api;
    }

    // Método para criar um novo endpoint e associá-lo à API
    public Endpoint createEndpoint(UUID apiId, EndpointDto endpointDto) {
        Api api = apiRepository.findById(apiId)
                .orElseThrow(() -> new IllegalArgumentException("API not found"));

        Endpoint endpoint = new Endpoint();
        endpoint.setName(endpointDto.name());
        endpoint.setUrl(endpointDto.url());
        endpoint.setType(endpointDto.type());
        endpoint.setDescription(endpointDto.description());
        endpoint.setApi(api);  // Associa o endpoint à API encontrada

        return endpointRepository.save(endpoint);
    }

    public List<EndpointDto> getEndpointsByApi(UUID apiId) {
        List<Endpoint> endpoints = endpointRepository.findByApiId(apiId);
        return endpoints.stream()
                .map(endpoint -> new EndpointDto(
                        endpoint.getId(),
                        endpoint.getName(),
                        endpoint.getUrl(),
                        endpoint.getType(),
                        endpoint.getDescription(),
                        EndpointDto.convertToParamsDtoList(endpoint.getParameters())))  // Chamada do método
                .collect(Collectors.toList());
    }

    // Método que adiciona o parâmetro ao endpoint
    public void addParamToEndpoint(UUID endpointId, ParameterDto parameterDto) {
        // Verifica se o endpoint existe
        Endpoint endpoint = endpointRepository.findById(endpointId)
                .orElseThrow(() -> new EntityNotFoundException("Endpoint not found with id: " + endpointId));

        // Cria um novo parâmetro e associa ao endpoint
        Parameter param = new Parameter();
        param.setName(parameterDto.name());
        param.setType(parameterDto.type());
        param.setOptional(parameterDto.optional());
        param.setDescription(parameterDto.description());
        param.setEndpoint(endpoint); // Associa o parâmetro ao endpoint

        // Salva o parâmetro no banco de dados
        parameterRepository.save(param);
    }

    // Método que retorna a lista de parâmetros de um endpoint específico
    public List<ParameterDto> getParametersByEndpoint(UUID endpointId) {
        // Verifica se o endpoint existe
        Endpoint endpoint = endpointRepository.findById(endpointId)
                .orElseThrow(() -> new EntityNotFoundException("Endpoint not found with id: " + endpointId));

        // Converte os parâmetros associados ao endpoint para DTOs e retorna
        return endpoint.getParameters().stream()
                .map(param -> new ParameterDto(
                        param.getName(),
                        param.getType(),
                        param.isOptional(),
                        param.getDescription()))
                .collect(Collectors.toList());
    }


    public void deleteParameterById(UUID parameterId) {

        var parameterExists = parameterRepository.existsById(parameterId);

        if (parameterExists) {
            parameterRepository.deleteById(parameterId);
        }
    }

    public void deleteEndpointById(UUID endpointId) {
        var endpointExists = endpointRepository.existsById(endpointId);

        if (endpointExists) {
            endpointRepository.deleteById(endpointId);
        }
    }

    // Função para converter a URL base para a URL interna do marketplace
    public String convertUrl(String baseUrl) {
        if (!baseUrl.startsWith("http://") && !baseUrl.startsWith("https://")) {
            baseUrl = "https://" + baseUrl;
        }
        return baseUrl.replace(".com", ".marketplace.com");
    }

    // Função que desabilita SSL e cria o RestTemplate
    private RestTemplate getRestTemplateWithDisabledSSL() {
        try {
            // Ignorar validações de certificado
            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        public X509Certificate[] getAcceptedIssuers() {
                            return null;
                        }

                        public void checkClientTrusted(X509Certificate[] certs, String authType) {}

                        public void checkServerTrusted(X509Certificate[] certs, String authType) {}
                    }
            };

            // Configura o contexto SSL com a estratégia que ignora SSL
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

            // Configura o HttpsURLConnection para ignorar a verificação de hostname
            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);

            // Usa SimpleClientHttpRequestFactory para configurar o RestTemplate
            SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
            factory.setBufferRequestBody(false); // Opcional: otimiza para requisições maiores

            return new RestTemplate(factory);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao desabilitar SSL", e);
        }
    }

    // Método para garantir que a URL é absoluta
    private String ensureAbsoluteUrl(String url) {
        // Verifica se a URL começa com "http://" ou "https://"
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            // Se não, adiciona "https://"
            return "https://" + url;
        }
        return url;
    }

    public String makeRequestToProviderApi(String apiId, String token, String endpoint, Map<String, String> queryParams) {
        // Converte o UUID para procurar a API no banco de dados
        var id = UUID.fromString(apiId);

        // Busca a API usando o ID
        Api api = apiRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("API not found"));

        // Usa a URL base da API
        String baseUrl = api.getBase_url();

        // Concatena o endpoint diretamente na URL base
        String absoluteUrl = ensureAbsoluteUrl(baseUrl) + endpoint;

        // Verifica se há parâmetros adicionais (sem apiId e endpoint)
        if (queryParams != null && !queryParams.isEmpty()) {
            String paramsString = queryParams.entrySet().stream()
                    .map(entry -> entry.getKey() + "=" + entry.getValue())
                    .collect(Collectors.joining("&"));
            absoluteUrl += "?" + paramsString; // Concatena os parâmetros à URL
        }

        System.out.println("Url: " + absoluteUrl); // Exibe a URL final para depuração

        // Cria o RestTemplate com SSL desabilitado
        RestTemplate restTemplate = getRestTemplateWithDisabledSSL();

        // Adiciona cabeçalhos, como o Authorization
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        // Faz a chamada para a API do provedor e retorna a resposta
        ResponseEntity<String> response = restTemplate.exchange(absoluteUrl, HttpMethod.GET, entity, String.class);
        return response.getBody();
    }
}
