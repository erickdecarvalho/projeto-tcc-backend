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
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.mail.MessagingException;
import jakarta.websocket.server.PathParam;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

import java.util.HashMap;
import java.util.Map;

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
            @ApiResponse(responseCode = "409", description = "Conflict email register",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"error\": \"Conflict email register\"}"))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"error\": \"Unexpected error occurred\"}")))
    })
    @PostMapping("/consumidores/registrar")
    public ResponseEntity<?> signupConsumer(@RequestBody SignupConsumerRequestDto signupConsumerRequestDto) {
        ConsumerDto createdConsumer = authService.signupConsumer(signupConsumerRequestDto);

        return new ResponseEntity<>(createdConsumer, HttpStatus.OK);
    }

    @Operation(summary = "Provider SignUp", description = "A provider register")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Provider successfully registered",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"message\": \"Provider registered successfully\", \"providerId\": \"12345\"}"))),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"error\": \"Invalid input\"}"))),
            @ApiResponse(responseCode = "409", description = "Conflict email register",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"error\": \"Conflict email register\"}"))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"error\": \"Unexpected error occurred\"}")))
    })
    @PostMapping("/provedores/registrar")
    public ResponseEntity<?> signupProvider(@RequestBody SignupProviderRequestDto signupProviderRequestDto) {
        ProviderDto createdProvider = authService.signupProvider(signupProviderRequestDto);

        return new ResponseEntity<>(createdProvider, HttpStatus.OK);
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
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest) throws IOException, JSONException {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    authenticationRequest.email(),
                    authenticationRequest.password()
            ));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("E-mail ou senha incorretos");
        }catch (Exception e ){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro interno do servidor");
        }

        final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.email());

        final String jwt = jwtUtil.generateToken(userDetails);
        User user = userRepository.findFirstByEmail(authenticationRequest.email());

        Map<String,Object> response = new HashMap<>();

        response.put("userId",user.getId());
        response.put("role",user.getRole());
        response.put("token","Bearer "+jwt);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Send Reset email", description = "Reset password by email send ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Email successfully send",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"userEmail\": \"jonh@email.com\"}"))),
            @ApiResponse(responseCode = "400", description = "Invalid credentials",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"error\": \"Invalid email or password\"}"))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"error\": \"Unexpected error occurred\"}")))
    })
    @PostMapping("/sendotp")
    public ResponseEntity<?> sendResetEmail(@RequestBody EmailSenderDto emailSenderDto) throws MessagingException {
        authService.sendPasswordResetEmail(emailSenderDto.userEmail());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Receive Reset password", description = "Receive Reset password by email and token ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Email successfully send",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"oldPassword\": \"oldPassword\",\"newestPassword\": \"newestPassword\", \"newestPasswordConfirm\": \"newestPasswordConfirm\" }"))),
            @ApiResponse(responseCode = "400", description = "Invalid credentials",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"error\": \"Invalid email or password\"}"))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"error\": \"Unexpected error occurred\"}")))
    })
    @PostMapping("/receiveotp/{email}/{token}")
    public ResponseEntity<?> receiveResetEmail(@PathVariable("email") String emailSenderDto,@PathVariable("token") String token, @RequestBody UpdatePasswordOutsiderDto updatePasswordOutsiderDto) {
        authService.resetPassword(emailSenderDto,token,updatePasswordOutsiderDto);
        return ResponseEntity.ok().build();
    }


}
