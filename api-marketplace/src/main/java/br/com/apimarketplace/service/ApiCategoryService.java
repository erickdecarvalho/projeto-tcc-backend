package br.com.apimarketplace.service;

import br.com.apimarketplace.dto.CreateApiCategory;
import br.com.apimarketplace.model.ApiCategory;
import br.com.apimarketplace.repository.ApiCategoryRepository;
import org.springframework.stereotype.Service;

@Service
public class ApiCategoryService {

    public ApiCategoryRepository apiCategoryRepository;

    public ApiCategoryService(ApiCategoryRepository apiCategoryRepository) {
        this.apiCategoryRepository = apiCategoryRepository;
    }

    public Integer createCategory(CreateApiCategory createApiCategory) {
        var entity = new ApiCategory(createApiCategory.name());

        var categorySaved = apiCategoryRepository.save(entity);
        return categorySaved.getId();
    }
}
