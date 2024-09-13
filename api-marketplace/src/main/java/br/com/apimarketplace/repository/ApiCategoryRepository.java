package br.com.apimarketplace.repository;

import br.com.apimarketplace.model.ApiCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApiCategoryRepository extends JpaRepository<ApiCategory, Integer> {
}
