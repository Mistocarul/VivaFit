package com.vivafit.vivafit.authentification.services;

import com.vivafit.vivafit.authentification.dto.LoginUserDto;
import com.vivafit.vivafit.authentification.dto.RegisterUserDto;
import com.vivafit.vivafit.authentification.entities.User;
import com.vivafit.vivafit.authentification.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class AuthenticationService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthenticationManager authenticationManager;

    public User registerUser(RegisterUserDto registerUserDto) {
        MultipartFile profilePicture = registerUserDto.getProfilePicture();
        String profilePicturePath = null;
        if(profilePicture != null && !profilePicture.isEmpty()){
            try {
                String originalFilename = profilePicture.getOriginalFilename();
                String extension = "";
                if(originalFilename != null && originalFilename.contains(".")){
                    extension = originalFilename.substring(originalFilename.lastIndexOf("."));
                }
                String filename = StringUtils.cleanPath(registerUserDto.getUsername() + extension);
                String uploadFolder = "C:/Users/Paul/Desktop/VivaFit/users-photos/";
                Path uploadFolderPath = Paths.get(uploadFolder);
                if(!Files.exists(uploadFolderPath)){
                    try {
                        Files.createDirectories(uploadFolderPath);
                    } catch (IOException e) {
                        System.err.println("Failed to create directory: " + uploadFolderPath.toString());
                        e.printStackTrace();
                        throw new RuntimeException("Failed to create directory", e);
                    }
                }
                Path filePath = uploadFolderPath.resolve(filename);
                profilePicture.transferTo(filePath.toFile());
                profilePicturePath = filePath.toString();
            } catch (IOException exception){
                throw new RuntimeException("Failed to upload profile picture", exception);
            }

        }
        User user = new User();
        user.setProfilePicture(profilePicturePath);
        user.setUsername(registerUserDto.getUsername());
        user.setPassword(passwordEncoder.encode(registerUserDto.getPassword()));
        user.setEmail(registerUserDto.getEmail());
        user.setPhoneNumber(registerUserDto.getPhoneNumber());
        user.setRole(registerUserDto.getRole());
        return userRepository.save(user);
    }

    public User loginUser(LoginUserDto loginUserDto) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginUserDto.getUsername(),
                        loginUserDto.getPassword()
                )
        );
        return userRepository.findByUsername(loginUserDto.getUsername()).orElseThrow();
    }
}
