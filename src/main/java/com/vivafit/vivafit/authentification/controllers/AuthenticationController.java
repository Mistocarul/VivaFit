package com.vivafit.vivafit.authentification.controllers;

import com.vivafit.vivafit.authentification.dto.ConfirmationCodeDto;
import com.vivafit.vivafit.authentification.dto.LoginUserDto;
import com.vivafit.vivafit.authentification.dto.RegisterUserDto;
import com.vivafit.vivafit.authentification.entities.User;
import com.vivafit.vivafit.authentification.responses.LoginResponse;
import com.vivafit.vivafit.authentification.responses.RegisterResponse;
import com.vivafit.vivafit.authentification.services.AuthenticationService;
import com.vivafit.vivafit.authentification.services.JwtService;
import com.vivafit.vivafit.authentification.services.TokenManagementService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RequestMapping("/api/auth")
@RestController
@Validated
public class AuthenticationController {
    @Autowired
    private JwtService jwtService;
    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private TokenManagementService tokenManagementService;

    @PostMapping("/signup")
    public ResponseEntity<RegisterResponse> register(@Valid @ModelAttribute RegisterUserDto registerUserDto) {
        //System.out.println("RegisterUserDto: " + registerUserDto);
        User user = authenticationService.registerUser(registerUserDto);
        RegisterResponse registerResponse = new RegisterResponse();
        registerResponse.setMessage("Confirmation email sent. Please check your email for further instructions.");
        registerResponse.setUser(user);
        String confirmPageUrl = "http://localhost:9090/confirm.html?username=" + user.getUsername();
        registerResponse.setRedirectPageUrl(confirmPageUrl);
        return ResponseEntity.ok(registerResponse);
    }

    @PostMapping("/confirm")
    public ResponseEntity<RegisterResponse> confirm(@Valid @RequestBody ConfirmationCodeDto confirmationCodeDto) {
        //System.out.println("Username: " + username + ", Code: " + code);
        String username = confirmationCodeDto.getUsername();
        //System.out.println("Username: " + username);
        int code = confirmationCodeDto.getCode();
        //System.out.println("Code: " + code);
        User user = authenticationService.confirmUser(username, code);
        RegisterResponse registerResponse = new RegisterResponse();
        if (user == null) {
            registerResponse.setMessage("Invalid confirmation code");
            registerResponse.setUser(user);
            return ResponseEntity.badRequest().body(registerResponse);
        }
        registerResponse.setMessage("User registered successfully");
        registerResponse.setUser(user);
        registerResponse.setRedirectPageUrl("http://localhost:9090/login.html");
        return ResponseEntity.ok(registerResponse);
    }

    @PostMapping("/cancel")
    public ResponseEntity<RegisterResponse> cancel(@Valid @RequestBody ConfirmationCodeDto confirmationCodeDto) {
        String username = confirmationCodeDto.getUsername();
        Integer code = confirmationCodeDto.getCode();
        authenticationService.cancelRegistration(username);
        RegisterResponse registerResponse = new RegisterResponse();
        registerResponse.setMessage("User registration canceled successfully");
        registerResponse.setUser(null);
        return ResponseEntity.ok(registerResponse);
    }

    @PostMapping("/signin")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginUserDto loginUserDto, BindingResult bindingResult){
        //System.out.println("LoginUserDto: " + loginUserDto);
        User user = authenticationService.loginUser(loginUserDto);
        String existingToken = tokenManagementService.getToken(user.getUsername());
        if (existingToken != null && jwtService.isTokenValid(existingToken, user)) {
            tokenManagementService.unregisterToken(user.getUsername());
            tokenManagementService.notifyUserOfDesconnection(user.getUsername());
        }
        String token = jwtService.generateToken(user);
        tokenManagementService.registerToken(user.getUsername(), token);
        //System.out.println("Token: " + token);
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setToken(token);
        loginResponse.setExpirationTime(jwtService.getExpirationTime());
        return ResponseEntity.ok(loginResponse);
    }
}
