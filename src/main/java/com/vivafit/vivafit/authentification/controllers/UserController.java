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
    public ResponseEntity<GeneralApiResponse> updateUser(@Valid @ModelAttribute UpdateUserInformationsDto updateUserInformationsDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        User updatedUser = userService.updateUserInformation(updateUserInformationsDto, currentUser);
        Authentication newAuthentication = new UsernamePasswordAuthenticationToken(updatedUser, updatedUser.getPassword(), updatedUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(newAuthentication);
        GeneralApiResponse response = new GeneralApiResponse("User updated successfully");
        return ResponseEntity.ok(response);
    }
}
