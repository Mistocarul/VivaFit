package com.vivafit.vivafit.authentification.controllers;

import com.vivafit.vivafit.authentification.dto.UpdateUserInformationsDto;
import com.vivafit.vivafit.authentification.dto.UserDto;
import com.vivafit.vivafit.authentification.entities.User;
import com.vivafit.vivafit.authentification.exceptions.InvalidTokenException;
import com.vivafit.vivafit.authentification.responses.GeneralApiResponse;
import com.vivafit.vivafit.authentification.responses.UpdateUserResponse;
import com.vivafit.vivafit.authentification.services.ConnectionDetailsService;
import com.vivafit.vivafit.authentification.services.JwtService;
import com.vivafit.vivafit.authentification.services.SignInTokenService;
import com.vivafit.vivafit.authentification.services.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Tag(name = "User", description = "User Controller")
@RequestMapping("/api/account")
@RestController
@Validated
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private SignInTokenService signInTokenService;
    @Autowired
    private ConnectionDetailsService connectionDetailsService;


    @GetMapping("/user-informations")
    public ResponseEntity<UserDto> authenticatedUser(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        User currentUser = jwtService.validateAndGetCurrentUser(authorizationHeader);
        UserDto userDto = new UserDto();
        userDto.setProfilePicturePath(currentUser.getProfilePicture());
        userDto.setUsername(currentUser.getUsername());
        userDto.setPhoneNumber(currentUser.getPhoneNumber());
        userDto.setNewPassword("");
        userDto.setCurrentPassword("");
        return ResponseEntity.ok(userDto);
    }

    @PutMapping("/update-user-informations")
    public ResponseEntity<UserDto> updateUserInformations(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
            @Valid @ModelAttribute UserDto userDto) throws IOException {
        User currentUser = jwtService.validateAndGetCurrentUser(authorizationHeader);
        UserDto updatedUserDto = userService.updateUserInformations(currentUser, userDto);
        return ResponseEntity.ok(updatedUserDto);
    }

    @GetMapping("/all-users")
    public ResponseEntity<List<User>> allUsers() {
        List<User> users = new ArrayList<>();
        users = userService.allUsers();
        return ResponseEntity.ok(users);
    }

    @DeleteMapping("/delete-user")
    public ResponseEntity<GeneralApiResponse> deleteUser(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        User currentUser = jwtService.validateAndGetCurrentUser(authorizationHeader);
        userService.deleteUser(currentUser);
        signInTokenService.unregisterToken(currentUser);
        connectionDetailsService.deleteConnectionDetails(currentUser);
        SecurityContextHolder.clearContext();
        GeneralApiResponse generalApiResponse = new GeneralApiResponse();
        generalApiResponse.setMessage("User account deleted successfully");
        return ResponseEntity.ok(generalApiResponse);
    }
}
