package com.vivafit.vivafit.authentification.services;

import com.vivafit.vivafit.authentification.dto.LoginUserDto;
import com.vivafit.vivafit.authentification.dto.RegisterUserDto;
import com.vivafit.vivafit.authentification.entities.User;
import com.vivafit.vivafit.authentification.exceptions.DataAlreadyExistsException;
import com.vivafit.vivafit.authentification.exceptions.InvalidFileTypeException;
import com.vivafit.vivafit.authentification.models.ConfirmationCode;
import com.vivafit.vivafit.authentification.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AuthenticationService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private EmailService emailService;

    private Map<String, User> pendingUsers = new ConcurrentHashMap<>();
    private Map<String, ConfirmationCode> confirmationCodes = new ConcurrentHashMap<>();

    @Value("${upload.folder.users-photos.path}")
    private String uploadFolderUsersPhotosPath;

    @Value("${upload.folder.users-folders.path}")
    private String uploadFolderUsersFoldersPath;

    public User registerUser(RegisterUserDto registerUserDto) {
        if (userRepository.existsByUsername(registerUserDto.getUsername())) {
            throw new DataAlreadyExistsException("Username is already taken");
        }
        if (userRepository.existsByEmail(registerUserDto.getEmail())) {
            throw new DataAlreadyExistsException("Email is already registered");
        }
        if (userRepository.existsByPhoneNumber(registerUserDto.getPhoneNumber())) {
            throw new DataAlreadyExistsException("Phone number is already registered");
        }
        MultipartFile profilePicture = registerUserDto.getProfilePicture();
        String profilePicturePath = null;
        if(profilePicture != null && !profilePicture.isEmpty()){
            try {
                String originalFilename = profilePicture.getOriginalFilename();
                String extension = "";
                if(originalFilename != null && originalFilename.contains(".")){
                    extension = originalFilename.substring(originalFilename.lastIndexOf("."));
                }
                if (!extension.equals(".jpg") && !extension.equals(".jpeg") && !extension.equals(".png") &&
                !extension.equals(".JPG") && !extension.equals(".JPEG") && !extension.equals(".PNG") && !extension.equals(".gif") && !extension.equals(".GIF")) {
                    throw new InvalidFileTypeException("Profile picture must be an image file with extension .jpg, .jpeg, .png, or .gif");
                }
                String filename = StringUtils.cleanPath(registerUserDto.getUsername() + extension);
                String uploadFolder = uploadFolderUsersPhotosPath;
                Path uploadFolderPath = Paths.get(uploadFolder);
                if(!Files.exists(uploadFolderPath)){
                    try {
                        Files.createDirectories(uploadFolderPath);
                    } catch (IOException e) {
                        throw new RuntimeException("Failed to create directory for users photos", e);
                    }
                }
                Path filePath = uploadFolderPath.resolve(filename);
                profilePicture.transferTo(filePath.toFile());
                profilePicturePath = filePath.toString();
            } catch (IOException exception){
                throw new RuntimeException("Failed to upload profile picture", exception);
            }
        }
        else{
            String defaultProfilePicturePath = uploadFolderUsersPhotosPath + "/default.png";
            File defaultImageFile = new File(defaultProfilePicturePath);
            String extension = ".png";
            String filename = StringUtils.cleanPath(registerUserDto.getUsername() + extension);
            Path uploadFolderPath = Paths.get(uploadFolderUsersPhotosPath);
            if (!Files.exists(uploadFolderPath)) {
                try {
                    Files.createDirectories(uploadFolderPath);
                } catch (IOException e) {
                    throw new RuntimeException("Failed to create directory for users photos", e);
                }
            }
            Path newFilePath = uploadFolderPath.resolve(filename);
            try {
                Files.copy(defaultImageFile.toPath(), newFilePath, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                throw new RuntimeException("Failed to copy default profile picture", e);
            }
            profilePicturePath = newFilePath.toString();
        }
        String uploadUserFolder = uploadFolderUsersFoldersPath + "/" + registerUserDto.getUsername();
        Path uploadUserFolderPath = Paths.get(uploadUserFolder);
        if(!Files.exists(uploadUserFolderPath)){
            try {
                Files.createDirectories(uploadUserFolderPath);
            } catch (IOException e) {
                throw new RuntimeException("Failed to create directory for users folders", e);
            }
        }

        User user = new User();
        user.setProfilePicture(profilePicturePath);
        user.setUsername(registerUserDto.getUsername());
        user.setPassword(passwordEncoder.encode(registerUserDto.getPassword()));
        user.setEmail(registerUserDto.getEmail());
        user.setPhoneNumber(registerUserDto.getPhoneNumber());
        user.setRole(registerUserDto.getRole());

        pendingUsers.remove(user.getUsername());
        confirmationCodes.remove(user.getUsername());

        emailService.setUser(user);
        emailService.sendEmail();

        pendingUsers.put(user.getUsername(), user);
        confirmationCodes.put(user.getUsername(), new ConfirmationCode(emailService.getCode(), LocalDateTime.now()));
        return user;
    }

    public User resendEmail(String username){
        User user = pendingUsers.get(username);
        if(user != null){
            confirmationCodes.remove(username);
            emailService.setUser(user);
            emailService.sendEmail();
            confirmationCodes.put(username, new ConfirmationCode(emailService.getCode(), LocalDateTime.now()));
            return user;
        }
        return null;
    }

    public User confirmUser(String username, int code){
        ConfirmationCode confirmationCode = confirmationCodes.get(username);
        if(confirmationCode == null){
            return null;
        }
        LocalDateTime now = LocalDateTime.now();
        if (Duration.between(confirmationCode.getCreationTime(), now).toMinutes() > 30) {
            confirmationCodes.remove(username);
            return null;
        }
        Integer codeConfirmation = confirmationCode.getCode();
        if(codeConfirmation != null && codeConfirmation == code){
            User user = pendingUsers.get(username);
            if(user != null){
                userRepository.save(user);
                pendingUsers.remove(username);
                confirmationCodes.remove(username);
                return user;
            }
        }
        return null;
    }

    public void cancelRegistration(String username){
        User user = pendingUsers.get(username);
        if(user != null){
            pendingUsers.remove(username);
            confirmationCodes.remove(username);
            Path profilePicturePath = Paths.get(user.getProfilePicture());
            Path uploadUserFolderPath = Paths.get(uploadFolderUsersFoldersPath + "/" + user.getUsername());
            try {
                Files.delete(profilePicturePath);
                Files.delete(uploadUserFolderPath);
            } catch (IOException e) {
                throw new RuntimeException("Failed to delete profile picture or user folder", e);
            }
        }
    }

    public User loginUser(LoginUserDto loginUserDto) {
        String identifier = loginUserDto.getIdentifier();
        User user = null;
        if (identifier.contains("@")){
            user = userRepository
                    .findByEmail(identifier)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + identifier));
        }
        else{
            user = userRepository
                    .findByUsername(identifier)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + identifier));
        }
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        user.getUsername(),
                        loginUserDto.getPassword()
                )
        );
        return user;
    }
}
