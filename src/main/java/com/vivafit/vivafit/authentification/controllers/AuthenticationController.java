package com.vivafit.vivafit.authentification.controllers;

import com.vivafit.vivafit.authentification.dto.LoginUserDto;
import com.vivafit.vivafit.authentification.dto.RegisterUserDto;
import com.vivafit.vivafit.authentification.entities.User;
import com.vivafit.vivafit.authentification.responses.LoginResponse;
import com.vivafit.vivafit.authentification.services.AuthenticationService;
import com.vivafit.vivafit.authentification.services.JwtService;
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

    @PostMapping("/signup")
    public ResponseEntity<User> register(@Valid @ModelAttribute RegisterUserDto registerUserDto) {
        //System.out.println("RegisterUserDto: " + registerUserDto);
        User user = authenticationService.registerUser(registerUserDto);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/signin")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginUserDto loginUserDto, BindingResult bindingResult){
        //System.out.println("LoginUserDto: " + loginUserDto);
        User user = authenticationService.loginUser(loginUserDto);
        String token = jwtService.generateToken(user);
        //System.out.println("Token: " + token);
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setToken(token);
        loginResponse.setExpirationTime(jwtService.getExpirationTime());
        return ResponseEntity.ok(loginResponse);
    }
}
