package br.com.apimarketplace.controller;

import br.com.apimarketplace.dto.*;
import br.com.apimarketplace.model.User;
import br.com.apimarketplace.repository.UserRepository;
import br.com.apimarketplace.service.AuthService;
import br.com.apimarketplace.service.jwt.UserDetailsServiceImpl;
import br.com.apimarketplace.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Tag(name = "Authentication Controller", description = "Related Auth Controller")
@Validated
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

    @Operation(summary = "Consumer SignUp", description = "A consumer register")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Consumer successfully registered",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"message\": \"Consumer registered successfully\", \"consumerId\": \"12345\"}"))),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"error\": \"Invalid input\"}"))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"error\": \"Unexpected error occurred\"}")))
    })
    @PostMapping("/consumidores/registrar")
    public ResponseEntity<?> signupConsumer(@RequestBody @Valid SignupConsumerRequestDto signupConsumerRequestDto) {
        return Optional.ofNullable(signupConsumerRequestDto)
                .map(authService::signupConsumer)
                .map(createdConsumer -> new ResponseEntity<>(createdConsumer, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.BAD_REQUEST));
    }

    @Operation(summary = "Provider SignUp", description = "A provider register")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Provider successfully registered",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"message\": \"Provider registered successfully\", \"providerId\": \"12345\"}"))),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"error\": \"Invalid input\"}"))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"error\": \"Unexpected error occurred\"}")))
    })
    @PostMapping("/provedores/registrar")
    public ResponseEntity<?> signupProvider(@RequestBody @Valid SignupProviderRequestDto signupProviderRequestDto) {
        return Optional.ofNullable(signupProviderRequestDto)
                .map(authService::signupProvider)
                .map(createdProvider -> new ResponseEntity<>(createdProvider, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.BAD_REQUEST));
    }

    @Operation(summary = "Authenticate email and password", description = "Email and password authentication method")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Authenticated successfully",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"userId\": \"12345\", \"role\": \"CONSUMER\", \"token\": \"Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...\"}"))),
            @ApiResponse(responseCode = "400", description = "Invalid credentials",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"error\": \"Invalid email or password\"}"))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"error\": \"Unexpected error occurred\"}")))
    })
    @PostMapping({"/authenticate"})
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest,
                                                       HttpServletResponse response) throws IOException {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    authenticationRequest.email(),
                    authenticationRequest.password()
            ));
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Invalid email or password");
        }

        final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.email());
        final String jwt = jwtUtil.generateToken(userDetails.getUsername());
        User user = userRepository.findFirstByEmail(authenticationRequest.email());

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("userId", user.getId());
        responseBody.put("role", user.getRole());
        responseBody.put("token", jwt);

        return ResponseEntity.ok()
                .header("Authorization", TOKEN_PREFIX + jwt)
                .body(responseBody);
    }
}