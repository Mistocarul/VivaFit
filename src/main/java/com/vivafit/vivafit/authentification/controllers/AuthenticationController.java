package com.vivafit.vivafit.authentification.controllers;

import com.vivafit.vivafit.authentification.dto.ConfirmationCodeDto;
import com.vivafit.vivafit.authentification.dto.ForgotPaswordDto;
import com.vivafit.vivafit.authentification.dto.LoginUserDto;
import com.vivafit.vivafit.authentification.dto.RegisterUserDto;
import com.vivafit.vivafit.authentification.entities.User;
import com.vivafit.vivafit.authentification.responses.GeneralApiResponse;
import com.vivafit.vivafit.authentification.responses.LoginResponse;
import com.vivafit.vivafit.authentification.responses.RegisterResponse;
import com.vivafit.vivafit.authentification.services.*;
import jakarta.servlet.http.HttpServletRequest;
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
    @Autowired
    private ConnectionDetailsService connectionDetailsService;
    @Autowired
    private LoginAttemptCacheService loginAttemptCacheService;

    @PostMapping("/signup")
    public ResponseEntity<RegisterResponse> register(@Valid @ModelAttribute RegisterUserDto registerUserDto) {
        User user = authenticationService.registerUser(registerUserDto);
        RegisterResponse registerResponse = new RegisterResponse();
        registerResponse.setMessage("Confirmation email sent. Please check your email for further instructions.");
        registerResponse.setUser(user);
        return ResponseEntity.ok(registerResponse);
    }

    @PostMapping("/confirm")
    public ResponseEntity<RegisterResponse> confirm(@Valid @RequestBody ConfirmationCodeDto confirmationCodeDto) {
        String username = confirmationCodeDto.getUsername();
        int code = confirmationCodeDto.getCode();
        User user = authenticationService.confirmUser(username, code);
        RegisterResponse registerResponse = new RegisterResponse();
        if (user == null) {
            registerResponse.setMessage("Invalid confirmation code");
            registerResponse.setUser(user);
            return ResponseEntity.badRequest().body(registerResponse);
        }
        registerResponse.setMessage("User registered successfully");
        registerResponse.setUser(user);
        return ResponseEntity.ok(registerResponse);
    }

    @PostMapping("/resend-email")
    public ResponseEntity<RegisterResponse> resendEmail(@Valid @RequestBody ConfirmationCodeDto confirmationCodeDto) {
        String username = confirmationCodeDto.getUsername();
        User user = authenticationService.resendEmail(username);
        RegisterResponse registerResponse = new RegisterResponse();
        registerResponse.setMessage("Confirmation email sent. Please check your email for further instructions.");
        registerResponse.setUser(user);
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
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginUserDto loginUserDto, HttpServletRequest request) {
        String ipAddress = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");

        System.out.println("IP Address: " + ipAddress);
        System.out.println("User Agent: " + userAgent);

        String identifier = loginUserDto.getIdentifier();
        LoginResponse loginResponse = new LoginResponse();
        User possibleUser = authenticationService.findUserByIdentifier(identifier);
        if (connectionDetailsService.isDifferentConnection(possibleUser.getUsername(), ipAddress, userAgent)) {
            loginAttemptCacheService.storeLoginAttempt(possibleUser.getUsername(), loginUserDto);
            authenticationService.sendEmailForNewBrowser(possibleUser.getUsername());
        }
        else{
            User user = authenticationService.loginUser(loginUserDto);

            String existingToken = tokenManagementService.getToken(user.getUsername());
            if (existingToken != null && jwtService.isTokenValid(existingToken, user)) {
                tokenManagementService.unregisterToken(user.getUsername());
                tokenManagementService.notifyUserOfDesconnection(user.getUsername());
            }
            String token = jwtService.generateToken(user);
            tokenManagementService.registerToken(user.getUsername(), token);

            loginResponse.setToken(token);
            loginResponse.setExpirationTime(jwtService.getExpirationTime());
            loginResponse.setUsername(user.getUsername());
            return ResponseEntity.ok(loginResponse);
        }
        loginResponse.setToken(null);
        loginResponse.setExpirationTime(0);
        loginResponse.setUsername(null);
        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/confirm-new-browser")
    public ResponseEntity<LoginResponse> confirmNewBrowser(@Valid @RequestBody ConfirmationCodeDto confirmationCodeDto, HttpServletRequest request) {
        String username = confirmationCodeDto.getUsername();
        int code = confirmationCodeDto.getCode();
        User user = authenticationService.confirmSignIn(username, code);
        LoginResponse loginResponse = new LoginResponse();
        if (user == null) {
            loginResponse.setToken(null);
            loginResponse.setExpirationTime(0);
            loginResponse.setUsername(null);
            return ResponseEntity.badRequest().body(loginResponse);
        }
        String ipAddress = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");
        LoginUserDto loginUserDto = loginAttemptCacheService.getLoginAttempt(username);
        user = authenticationService.loginUser(loginUserDto);
        String existingToken = tokenManagementService.getToken(user.getUsername());
        if (existingToken != null && jwtService.isTokenValid(existingToken, user)) {
            tokenManagementService.unregisterToken(user.getUsername());
            tokenManagementService.notifyUserOfDesconnection(user.getUsername());
        }
        String token = jwtService.generateToken(user);
        tokenManagementService.registerToken(user.getUsername(), token);
        loginAttemptCacheService.removeLoginAttempt(username);
        if(loginUserDto.getRememberBrowser().contains("true")) {
            connectionDetailsService.saveConnectionDetails(user.getUsername(), ipAddress, userAgent);
        }
        loginResponse.setToken(token);
        loginResponse.setExpirationTime(jwtService.getExpirationTime());
        loginResponse.setUsername(user.getUsername());
        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/cancel-signin")
    public ResponseEntity<LoginResponse> cancelSignIn(@Valid @RequestBody ConfirmationCodeDto confirmationCodeDto) {
        String username = confirmationCodeDto.getUsername();
        Integer code = confirmationCodeDto.getCode();
        authenticationService.cancelSignIn(username);
        loginAttemptCacheService.removeLoginAttempt(username);
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setToken(null);
        loginResponse.setExpirationTime(0);
        loginResponse.setUsername(null);
        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<GeneralApiResponse> forgotPassword(@Valid @RequestBody ForgotPaswordDto forgotPaswordDto) {
        String email = forgotPaswordDto.getEmail();
        authenticationService.forgotPassword(email);
        GeneralApiResponse generalApiResponse = new GeneralApiResponse();
        generalApiResponse.setMessage("Password reset email sent. Please check your email for further instructions.");
        return ResponseEntity.ok(generalApiResponse);
    }
}
