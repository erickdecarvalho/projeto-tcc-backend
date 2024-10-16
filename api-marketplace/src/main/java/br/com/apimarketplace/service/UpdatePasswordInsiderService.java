package br.com.apimarketplace.service;

import br.com.apimarketplace.dto.UpdatePasswordInsiderDto;
import br.com.apimarketplace.model.Consumer;
import br.com.apimarketplace.model.Provider;
import br.com.apimarketplace.model.User;
import br.com.apimarketplace.repository.ConsumerRepository;
import br.com.apimarketplace.repository.ProviderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UpdatePasswordInsiderService {

    @Autowired
    private  ProviderRepository providerRepository;

    @Autowired
    private  ConsumerRepository consumerRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

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

        if (!passwordEncoder.matches(updatePasswordInsiderDto.oldPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Senha antiga incorreta");
        }

        if (passwordEncoder.matches(updatePasswordInsiderDto.newestPassword(), user.getPassword())) {
            throw new IllegalArgumentException("A nova senha não pode ser igual à senha anterior");
        }

        user.setPassword(passwordEncoder.encode(updatePasswordInsiderDto.newestPassword()));
    }

    private boolean isEqualNewestPassword(String newestPassword, String newestPasswordConfirm){
        return newestPassword.equals(newestPasswordConfirm);
    }



}
