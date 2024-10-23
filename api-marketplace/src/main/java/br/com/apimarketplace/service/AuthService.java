package br.com.apimarketplace.service;

import br.com.apimarketplace.dto.*;
import br.com.apimarketplace.enums.UserRole;
import br.com.apimarketplace.model.Consumer;
import br.com.apimarketplace.model.Provider;
import br.com.apimarketplace.model.ResetToken;
import br.com.apimarketplace.model.User;
import br.com.apimarketplace.repository.ConsumerRepository;
import br.com.apimarketplace.repository.ProviderRepository;
import br.com.apimarketplace.repository.ResetTokenRepository;
import br.com.apimarketplace.repository.UserRepository;
import br.com.apimarketplace.service.mail.MailService;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private  ConsumerRepository consumerRepository;

    @Autowired
    private  ProviderRepository providerRepository;

    @Autowired
    private MailService mailService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UpdatePasswordService updatePasswordService;

    @Autowired
    private ResetTokenRepository resetTokenRepository;


    public ConsumerDto signupConsumer(SignupConsumerRequestDto signupConsumerRequestDto) {
        Optional<Consumer> optionalConsumer= consumerRepository.findByEmail(signupConsumerRequestDto.email());
        if (optionalConsumer.isPresent()){
            throw new RuntimeException("E-mail já cadastrado"); /*CRIAR CLASSE EXCEPTIONS*/
        }


        Consumer consumer = new Consumer();
        consumer.setUsername(signupConsumerRequestDto.username());
        consumer.setPassword(new BCryptPasswordEncoder().encode(signupConsumerRequestDto.password()));
        consumer.setEmail(signupConsumerRequestDto.email());
        consumer.setRole(UserRole.CONSUMER);

        return consumerRepository.save(consumer).getDto();
    }

    public ProviderDto signupProvider(SignupProviderRequestDto signupProviderRequestDto) {
        Optional<Provider> providerOptional = providerRepository.findByEmail(signupProviderRequestDto.email());
        if (providerOptional.isPresent()){
            throw new RuntimeException("E-mail já cadastrado");
        }

        Provider provider = new Provider();

        provider.setUsername(signupProviderRequestDto.username());
        provider.setPassword(new BCryptPasswordEncoder().encode(signupProviderRequestDto.password()));
        provider.setEmail(signupProviderRequestDto.email());
        provider.setOrganizationName(signupProviderRequestDto.organizationName());

        provider.setRole(UserRole.PROVIDER);

        return providerRepository.save(provider).getDto();
    }

    public void sendPasswordResetEmail(String userEmail) throws MessagingException {
        if (userEmail.isEmpty()) {
            throw new RuntimeException("E-mail cannot be null");
        }
        User user = userRepository.findFirstByEmail(userEmail);
        if (user.getEmail().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        ResetToken resetToken = updatePasswordService.createResetTokenForUser(user);

        mailService.sendEmailText(user.getEmail(), resetToken);
    }

    public void resetPassword(String emailUser,String token, UpdatePasswordOutsiderDto updatePasswordOutsiderDto){

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        User user = userRepository.findFirstByEmail(emailUser);

        if (user == null){
            throw new IllegalArgumentException("User não encontrado");
        }

        if(!updatePasswordService.isEqualOlderPassword(updatePasswordOutsiderDto.oldPassword(), user.getPassword())){
            throw new IllegalArgumentException("Senha antiga incorreta");
        }
        if (updatePasswordService.isEqualOlderPassword(updatePasswordOutsiderDto.newestPassword(),user.getPassword())){
            throw new IllegalArgumentException("A nova senha não pode ser igual à senha anterior");
        }

        if (!updatePasswordService.isEqualNewestPassword(updatePasswordOutsiderDto.newestPassword(), updatePasswordOutsiderDto.newestPasswordConfirm())){
            throw new IllegalArgumentException("A nova senha não foi confirmada corretamente");
        }

        ResetToken resetToken = resetTokenRepository.findByToken(token);
        if (resetToken == null || !resetToken.getUser().getEmail().equals(emailUser)){
            throw new IllegalArgumentException("Token invalido");
        }
        user.setPassword(passwordEncoder.encode(updatePasswordOutsiderDto.newestPassword()));
        userRepository.save(user);

    }


}
