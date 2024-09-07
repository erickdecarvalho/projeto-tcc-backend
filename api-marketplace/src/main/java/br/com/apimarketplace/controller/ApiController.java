package br.com.apimarketplace.controller;

import br.com.apimarketplace.dto.CreateApiDto;
import br.com.apimarketplace.dto.UpdateApiDto;
import br.com.apimarketplace.model.Api;
import br.com.apimarketplace.service.ApiService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/apis")
public class ApiController {

    private ApiService apiService;

    public ApiController(ApiService apiService) {
        this.apiService = apiService;
    }

    @PostMapping
    public ResponseEntity<Api> createApi(@RequestBody CreateApiDto createApiDto) {
        var apiId = apiService.createApi(createApiDto);
        return ResponseEntity.created(URI.create("/apis/" + apiId.toString())).build();
    }

    @GetMapping("/{apiId}")
    public ResponseEntity<Api> getApiById(@PathVariable("apiId") String userId) {
        var api = apiService.getApiById(userId);

        if (api.isPresent()) {
            return ResponseEntity.ok(api.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<Api>> listApis() {
        var apis = apiService.listApis();

        return ResponseEntity.ok(apis);
    }

    @PutMapping("/{apiId}")
    public ResponseEntity<Void> updateApi(@PathVariable("apiId") String id, @RequestBody UpdateApiDto updateApiDto) {
        apiService.updateApiById(id, updateApiDto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{apiId}")
    public ResponseEntity<Void> deleteApi(@PathVariable("apiId") String id) {
        apiService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
