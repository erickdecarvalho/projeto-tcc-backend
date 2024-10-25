package br.com.apimarketplace.repository;

import br.com.apimarketplace.model.ResetToken;
import br.com.apimarketplace.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResetTokenRepository extends JpaRepository<ResetToken,Long> {
    ResetToken findByToken(String token);

    ResetToken findByUser(User user);
}
