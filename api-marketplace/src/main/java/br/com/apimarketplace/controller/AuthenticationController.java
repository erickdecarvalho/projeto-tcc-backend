package br.com.apimarketplace.controller;

import br.com.apimarketplace.dto.*;
import br.com.apimarketplace.model.User;
import br.com.apimarketplace.repository.UserRepository;
import br.com.apimarketplace.service.AuthService;
import br.com.apimarketplace.service.jwt.UserDetailsServiceImpl;
import br.com.apimarketplace.util.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

import java.io.IOException;

@RestController
public class AuthenticationController {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private AuthenticationManager authenticationManager;



    public static final String TOKEN_PREFIX = "Bearer ";

    public static final String HEADER_STRING = "Authorization";

    @PostMapping("/consumidores/registrar")
    public ResponseEntity<?> signupConsumer(@RequestBody SignupConsumerRequestDto signupConsumerRequestDto) {
        ConsumerDto createdConsumer = authService.signupConsumer(signupConsumerRequestDto);

        return new ResponseEntity<>(createdConsumer, HttpStatus.OK);
    }

    @PostMapping("/provedores/registrar")
    public ResponseEntity<?> signupProvider(@RequestBody SignupProviderRequestDto signupProviderRequestDto) {
        ProviderDto createdProvider = authService.signupProvider(signupProviderRequestDto);

        return new ResponseEntity<>(createdProvider, HttpStatus.OK);
    }

    @PostMapping({"/authenticate"})
    public void createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest,
                                          HttpServletResponse response) throws IOException, JSONException {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    authenticationRequest.email(),
                    authenticationRequest.password()
            ));
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("E-mail ou senha incorretos");
        }

        final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.email());

        final String jwt = jwtUtil.generateToken(userDetails.getUsername());
        User user = userRepository.findFirstByEmail(authenticationRequest.email());

        response.getWriter().write(new JSONObject()
                .put("userId", user.getId())
                .put("role", user.getRole())
                .toString()
        );

        response.addHeader("Access-Control-Expose-Headers", "Authorization");
        response.addHeader("Access-Control-Allow-Headers", "Authorization," +
                " X-PINGOTHER, Origin, X-Request-With, Content-Type, Accept, X-Custom-Header");

        response.addHeader(HEADER_STRING, TOKEN_PREFIX+jwt);
    }
}
