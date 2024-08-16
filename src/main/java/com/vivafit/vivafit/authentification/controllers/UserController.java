package com.vivafit.vivafit.authentification.controllers;

import com.vivafit.vivafit.authentification.dto.UpdateUserInformationsDto;
import com.vivafit.vivafit.authentification.entities.User;
import com.vivafit.vivafit.authentification.exceptions.InvalidTokenException;
import com.vivafit.vivafit.authentification.responses.GeneralApiResponse;
import com.vivafit.vivafit.authentification.responses.UpdateUserResponse;
import com.vivafit.vivafit.authentification.services.JwtService;
import com.vivafit.vivafit.authentification.services.SignInTokenService;
import com.vivafit.vivafit.authentification.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RequestMapping("/api/account")
@RestController
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private SignInTokenService signInTokenService;


    @GetMapping("/user-informations")
    public ResponseEntity<User> authenticatedUser(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        if(authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")){
            throw new InvalidTokenException("Invalid token");
        }
        String jwtToken = authorizationHeader.substring(7);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        String existingToken = signInTokenService.getToken(currentUser);
        if (existingToken != null && jwtService.isTokenValid(existingToken, currentUser) && jwtToken.equals(existingToken)) {
            return ResponseEntity.ok(currentUser);
        }
        else{
            throw new InvalidTokenException("Invalid token");
        }
    }

    @GetMapping("/all-users")
    public ResponseEntity<List<User>> allUsers() {
        List<User> users = new ArrayList<>();
        users = userService.allUsers();
        return ResponseEntity.ok(users);
    }

    @DeleteMapping("/delete-user")
    public ResponseEntity<GeneralApiResponse> deleteUser(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        if(authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")){
            throw new InvalidTokenException("Invalid token");
        }
        String jwtToken = authorizationHeader.substring(7);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        String existingToken = signInTokenService.getToken(currentUser);
        if (existingToken != null && jwtService.isTokenValid(existingToken, currentUser) && jwtToken.equals(existingToken)) {
            userService.deleteUser(currentUser);
        }
        else{
            throw new InvalidTokenException("Invalid token");
        }
        SecurityContextHolder.clearContext();
        GeneralApiResponse response = new GeneralApiResponse("User deleted successfully");
        return ResponseEntity.ok(response);
    }

    @PutMapping("/update-user")
    public ResponseEntity<UpdateUserResponse> updateUser(@ModelAttribute UpdateUserInformationsDto updateUserInformationsDto,
                                                         @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        if(authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")){
            throw new InvalidTokenException("Invalid token");
        }
        String jwtToken = authorizationHeader.substring(7);
        userService.validateUserUpdateInformations(updateUserInformationsDto);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        User updatedUser = userService.updateUserInformation(updateUserInformationsDto, currentUser);
        String passwordToUse = "";
        if(updateUserInformationsDto.getNewPassword() != null && !updateUserInformationsDto.getNewPassword().isEmpty()){
            passwordToUse = updateUserInformationsDto.getNewPassword();
        } else {
            passwordToUse = updateUserInformationsDto.getCurrentPassword();
        }
        Authentication newAuthentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(updatedUser.getUsername(), passwordToUse)
        );
        SecurityContextHolder.getContext().setAuthentication(newAuthentication);
        String existingToken = signInTokenService.getToken(currentUser);
        if (existingToken != null && jwtService.isTokenValid(existingToken, currentUser) && jwtToken.equals(existingToken)) {
            signInTokenService.unregisterToken(currentUser);
        }
        String token = jwtService.generateToken(updatedUser);
        LocalDateTime expiryDate = LocalDateTime.now().plusSeconds(jwtService.getExpirationTime()/1000);
        signInTokenService.registerToken(updatedUser, token, expiryDate);
        UpdateUserResponse response = new UpdateUserResponse("User updated successfully", token, jwtService.getExpirationTime(), updatedUser.getUsername());
        return ResponseEntity.ok(response);
    }
}
