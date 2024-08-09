package com.vivafit.vivafit.authentification.controllers;

import com.vivafit.vivafit.authentification.dto.UpdateUserInformationsDto;
import com.vivafit.vivafit.authentification.entities.User;
import com.vivafit.vivafit.authentification.responses.GeneralApiResponse;
import com.vivafit.vivafit.authentification.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

    @GetMapping("/me")
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
    public ResponseEntity<GeneralApiResponse> updateUser(@ModelAttribute UpdateUserInformationsDto updateUserInformationsDto) {
        if(updateUserInformationsDto.getNewUsername() != null && !updateUserInformationsDto.getNewUsername().isEmpty()){
            if(updateUserInformationsDto.getNewUsername().length() < 4 || updateUserInformationsDto.getNewUsername().length() > 24){
                throw new IllegalArgumentException("Username must be between 4 and 24 characters");
            }
            if(updateUserInformationsDto.getNewUsername().contains("@")){
                throw new IllegalArgumentException("Username must not contain '@'");
            }
        }
        if(updateUserInformationsDto.getNewPassword() != null && !updateUserInformationsDto.getNewPassword().isEmpty()){
            if(updateUserInformationsDto.getNewPassword().length() < 8){
                throw new IllegalArgumentException("Password must be at least 8 characters long");
            }
            if(!updateUserInformationsDto.getNewPassword().matches("^(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*(),.?\":{}|<>]).{8,}$")){
                throw new IllegalArgumentException("Password must contain at least one uppercase letter, one digit, and one special character");
            }
        }
        if(updateUserInformationsDto.getNewEmail() != null && !updateUserInformationsDto.getNewEmail().isEmpty()){
            if(!updateUserInformationsDto.getNewEmail().matches("^(.+)@(.+)$")){
                throw new IllegalArgumentException("The email address is invalid.");
            }
        }
        if(updateUserInformationsDto.getNewPhoneNumber() != null && !updateUserInformationsDto.getNewPhoneNumber().isEmpty()){
            if(!updateUserInformationsDto.getNewPhoneNumber().startsWith("+")){
                throw new IllegalArgumentException("Phone number must start with '+' sign");
            }
            if(updateUserInformationsDto.getNewPhoneNumber().length() < 6 || updateUserInformationsDto.getNewPhoneNumber().length() > 15){
                throw new IllegalArgumentException("Phone number must be between 6 and 15 characters");
            }
        }
        if (updateUserInformationsDto.getCurrentPassword() == null || updateUserInformationsDto.getCurrentPassword().isEmpty()) {
            throw new IllegalArgumentException("Password is required");
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        User updatedUser = userService.updateUserInformation(updateUserInformationsDto, currentUser);
        Authentication newAuthentication = new UsernamePasswordAuthenticationToken(updatedUser, updatedUser.getPassword(), updatedUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(newAuthentication);
        GeneralApiResponse response = new GeneralApiResponse("User updated successfully");
        return ResponseEntity.ok(response);
    }
}
