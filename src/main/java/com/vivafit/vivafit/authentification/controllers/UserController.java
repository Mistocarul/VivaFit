package com.vivafit.vivafit.authentification.controllers;

import com.vivafit.vivafit.authentification.dto.UpdateUserInformationsDto;
import com.vivafit.vivafit.authentification.entities.User;
import com.vivafit.vivafit.authentification.responses.GeneralApiResponse;
import com.vivafit.vivafit.authentification.responses.LoginResponse;
import com.vivafit.vivafit.authentification.responses.UpdateUserResponse;
import com.vivafit.vivafit.authentification.services.JwtService;
import com.vivafit.vivafit.authentification.services.TokenManagementService;
import com.vivafit.vivafit.authentification.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

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
    private TokenManagementService tokenManagementService;


    @GetMapping("/user-informations")
    public ResponseEntity<User> authenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        return ResponseEntity.ok(currentUser);
    }

    @GetMapping("/all-users")
    public ResponseEntity<List<User>> allUsers() {
        List<User> users = new ArrayList<>();
        users = userService.allUsers();
        return ResponseEntity.ok(users);
    }

    @DeleteMapping("/delete-user")
    public ResponseEntity<GeneralApiResponse> deleteUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        userService.deleteUser(currentUser);
        SecurityContextHolder.clearContext();
        GeneralApiResponse response = new GeneralApiResponse("User deleted successfully");
        return ResponseEntity.ok(response);
    }

    @PutMapping("/update-user")
    public ResponseEntity<UpdateUserResponse> updateUser(@ModelAttribute UpdateUserInformationsDto updateUserInformationsDto) {
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
        String existingToken = tokenManagementService.getToken(currentUser.getUsername());
        if (existingToken != null && jwtService.isTokenValid(existingToken, currentUser)) {
            tokenManagementService.unregisterToken(currentUser.getUsername());
        }
        String token = jwtService.generateToken(updatedUser);
        tokenManagementService.registerToken(updatedUser.getUsername(), token);
        UpdateUserResponse response = new UpdateUserResponse("User updated successfully", token, jwtService.getExpirationTime(), updatedUser.getUsername());
        return ResponseEntity.ok(response);
    }
}
