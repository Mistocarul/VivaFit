package com.vivafit.vivafit.authentification.services;

import com.vivafit.vivafit.authentification.dto.UpdateUserInformationsDto;
import com.vivafit.vivafit.authentification.dto.UserDto;
import com.vivafit.vivafit.authentification.entities.User;
import com.vivafit.vivafit.authentification.repositories.UserRepository;
import com.vivafit.vivafit.specialist.entities.Specialist;
import com.vivafit.vivafit.specialist.repositories.SpecialistRepository;
import com.vivafit.vivafit.specialist.services.SpecialistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private SignInTokenService signInTokenService;
    @Autowired
    private SpecialistRepository specialistRepository;


    @Value("${upload.folder.users-photos.path}")
    private String uploadFolderUsersPhotosPath;
    @Value("${upload.folder.users-folders.path}")
    private String uploadFolderUsersFoldersPath;

    public List<User> allUsers() {
        return userRepository.findAll();
    }

    public void deleteUser(User user) {
        String userProfilePicture = user.getProfilePicture();
        try {
            Path filePath = Paths.get(userProfilePicture);
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete profile picture", e);
        }
        userRepository.delete(user);
    }

    public UserDto updateUserInformations(User user, UserDto userDto) throws IOException {
        if ((userDto.getCurrentPassword() == null || userDto.getCurrentPassword().isEmpty()) && userDto.getCreatedWith().equals("OWN_METHOD")) {
            throw new IllegalArgumentException("Current password is required");
        }
        if (!passwordEncoder.matches(userDto.getCurrentPassword(), user.getPassword()) && userDto.getCreatedWith().equals("OWN_METHOD")) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        Optional<User> possibleUser = Optional.ofNullable(userRepository.findByUsername(userDto.getUsername()).orElse(null));
        Optional<User> possibleUserWithPhoneNumber = Optional.ofNullable(userRepository.findByPhoneNumber(userDto.getPhoneNumber()).orElse(null));

        if (possibleUser.isPresent() && !possibleUser.get().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (possibleUserWithPhoneNumber.isPresent() && !possibleUserWithPhoneNumber.get().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Phone number already exists");
        }

        signInTokenService.unregisterToken(user);
        SecurityContextHolder.clearContext();

        if (userDto.getNewProfilePicture() != null && !userDto.getNewProfilePicture().isEmpty()) {
            Path filePath = Paths.get(user.getProfilePicture());
            Files.copy(userDto.getNewProfilePicture().getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        }
        user.setUsername(userDto.getUsername());
        user.setPhoneNumber(userDto.getPhoneNumber());
        if (userDto.getNewPassword() != null && !userDto.getNewPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userDto.getNewPassword()));
        }

        userRepository.save(user);


        if (user.getRole().equals("COACH") || user.getRole().equals("NUTRITIONIST")) {
            Optional<Specialist> optionalSpecialist = specialistRepository.findByUserId(user.getId());
            if (optionalSpecialist.isPresent()) {
                Specialist specialist = optionalSpecialist.get();
                specialist.setPhoneNumber(user.getPhoneNumber());
                specialist.setProfilePicture(user.getProfilePicture());
                specialistRepository.save(specialist);
            }
        }

        UserDto updatedUserDto = new UserDto();
        updatedUserDto.setProfilePicturePath(user.getProfilePicture());
        updatedUserDto.setUsername(user.getUsername());
        updatedUserDto.setPhoneNumber(user.getPhoneNumber());
        updatedUserDto.setNewPassword("");
        updatedUserDto.setCurrentPassword("");
        return updatedUserDto;
    }
}
