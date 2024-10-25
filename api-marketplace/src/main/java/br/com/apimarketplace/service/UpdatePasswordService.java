package br.com.apimarketplace.service;

import br.com.apimarketplace.dto.UpdatePasswordInsiderDto;
import br.com.apimarketplace.model.Consumer;
import br.com.apimarketplace.model.Provider;
import br.com.apimarketplace.model.ResetToken;
import br.com.apimarketplace.model.User;
import br.com.apimarketplace.repository.ConsumerRepository;
import br.com.apimarketplace.repository.ProviderRepository;
import br.com.apimarketplace.repository.ResetTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class UpdatePasswordService {

    @Autowired
    private  ProviderRepository providerRepository;

    @Autowired
    private  ConsumerRepository consumerRepository;


    @Autowired
    private ResetTokenRepository resetTokenRepository;

    public void updatePasswordInsider(UpdatePasswordInsiderDto updatePasswordInsiderDto) {

        if (!isEqualNewestPassword(updatePasswordInsiderDto.newestPassword(), updatePasswordInsiderDto.newestPasswordConfirm())) {
            throw new IllegalArgumentException("Novas senhas não são iguais");
        }

        boolean userFound = false;

        Optional<Provider> optionalProvider = providerRepository.findById(updatePasswordInsiderDto.userId());
        if (optionalProvider.isPresent()) {
            Provider provider = optionalProvider.get();
            updateUserPassword(provider, updatePasswordInsiderDto);
            providerRepository.save(provider);
            userFound = true;
        } else {
            Optional<Consumer> optionalConsumer = consumerRepository.findById(updatePasswordInsiderDto.userId());
            if (optionalConsumer.isPresent()) {
                Consumer consumer = optionalConsumer.get();
                updateUserPassword(consumer, updatePasswordInsiderDto);
                consumerRepository.save(consumer);
                userFound = true;
            }
        }

        if (!userFound) {
            throw new IllegalArgumentException("Usuário não encontrado");
        }
    }

    private void updateUserPassword(User user, UpdatePasswordInsiderDto updatePasswordInsiderDto) {

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        if (!isEqualOlderPassword(updatePasswordInsiderDto.oldPassword(),user.getPassword())) {
            throw new IllegalArgumentException("Senha antiga incorreta");
        }

        if (isEqualOlderPassword(updatePasswordInsiderDto.newestPassword(), user.getPassword())) {
            throw new IllegalArgumentException("A nova senha não pode ser igual à senha anterior");
        }

        user.setPassword(passwordEncoder.encode(updatePasswordInsiderDto.newestPassword()));
    }

    public boolean isEqualOlderPassword(String password, String userOldPassword){
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.matches(password,userOldPassword);
    }

    public boolean isEqualNewestPassword(String newestPassword, String newestPasswordConfirm){

        return newestPassword.equals(newestPasswordConfirm);
    }

    public ResetToken createResetTokenForUser(User user){
        ResetToken resetToken = resetTokenRepository.findByUser(user);
        String token = UUID.randomUUID().toString();
        if (resetToken!=null){
            resetToken.setToken(token);
        }else {
            resetToken = new ResetToken(user,token);
        }
        return resetTokenRepository.save(resetToken);
    }

    public ResetToken getResetToken(String token){
        return resetTokenRepository.findByToken(token);
    }


}
