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
    @Autowired
    private ConfirmationAuthService confirmationAuthService;

    @Value("${upload.folder.users-photos.path}")
    private String uploadFolderUsersPhotosPath;
    @Value("${upload.folder.users-folders.path}")
    private String uploadFolderUsersFoldersPath;
    @Value("${upload.temporal.multipart.folder}")
    private String uploadTemporalMultipartFolder;

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
        User user = new User();
        user.setUsername(registerUserDto.getUsername());
        user.setPassword(passwordEncoder.encode(registerUserDto.getPassword()));
        user.setEmail(registerUserDto.getEmail());
        user.setPhoneNumber(registerUserDto.getPhoneNumber());
        user.setRole(registerUserDto.getRole());

        confirmationAuthService.removePendingUser(user.getUsername());
        confirmationAuthService.removeConfirmationCode(user.getUsername());

        emailService.setUser(user);
        emailService.sendEmail();

        confirmationAuthService.addPendingUser(user.getUsername(), user);
        confirmationAuthService.addConfirmationCode(user.getUsername(), new ConfirmationCode(emailService.getCode(), LocalDateTime.now()));

        MultipartFile profilePicture = registerUserDto.getProfilePicture();
        if (profilePicture != null && !profilePicture.isEmpty()) {
            try {
                Path temporalMultipartFolderPath = Paths.get(uploadTemporalMultipartFolder);
                if (!Files.exists(temporalMultipartFolderPath)) {
                    try {
                        Files.createDirectories(temporalMultipartFolderPath);
                    } catch (IOException e) {
                        throw new RuntimeException("Failed to create directory for temporal multipart files", e);
                    }
                }
                String filename = registerUserDto.getUsername() + "-temporal-" + profilePicture.getOriginalFilename();
                Path filePath = temporalMultipartFolderPath.resolve(filename);
                profilePicture.transferTo(filePath.toFile());
                String temporalMultipartFilePath = filePath.toString();
                confirmationAuthService.addProfilePicture(registerUserDto.getUsername(), temporalMultipartFilePath);
            } catch (IOException e) {
                throw new RuntimeException("Failed to upload profile picture", e);
            }
        }
        return user;
    }

    public void sendEmailForNewBrowser(String username) {
        User user = userRepository
                .findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
        confirmationAuthService.removePendingUser(user.getUsername());
        confirmationAuthService.removeConfirmationCode(user.getUsername());
        emailService.setUser(user);
        emailService.sendEmail();
        confirmationAuthService.addPendingUser(user.getUsername(), user);
        confirmationAuthService.addConfirmationCode(user.getUsername(), new ConfirmationCode(emailService.getCode(), LocalDateTime.now()));
    }

    public User resendEmail(String username){
        User user = confirmationAuthService.getPendingUser(username);
        if(user != null){
            confirmationAuthService.removeConfirmationCode(username);
            emailService.setUser(user);
            emailService.sendEmail();
            confirmationAuthService.addConfirmationCode(username, new ConfirmationCode(emailService.getCode(), LocalDateTime.now()));
            return user;
        }
        return null;
    }

    public User confirmSignIn(String username, int code) {
        ConfirmationCode confirmationCode = confirmationAuthService.getConfirmationCode(username);
        if (confirmationCode == null) {
            return null;
        }
        LocalDateTime now = LocalDateTime.now();
        if (Duration.between(confirmationCode.getCreationTime(), now).toMinutes() > 30) {
            confirmationAuthService.removeConfirmationCode(username);
            return null;
        }
        Integer codeConfirmation = confirmationCode.getCode();
        if (codeConfirmation != null && codeConfirmation == code) {
            User user = confirmationAuthService.getPendingUser(username);
            if (user != null) {
                confirmationAuthService.removePendingUser(username);
                confirmationAuthService.removeConfirmationCode(username);
                return user;
            }
        }
        return null;
    }

    public User confirmUser(String username, int code){
        ConfirmationCode confirmationCode = confirmationAuthService.getConfirmationCode(username);
        if(confirmationCode == null){
            return null;
        }
        LocalDateTime now = LocalDateTime.now();
        if (Duration.between(confirmationCode.getCreationTime(), now).toMinutes() > 30) {
            confirmationAuthService.removePendingUser(username);
            return null;
        }
        Integer codeConfirmation = confirmationCode.getCode();
        if(codeConfirmation != null && codeConfirmation == code){
            User user = confirmationAuthService.getPendingUser(username);
            if(user != null){
                String profilePictureTemporalPath = confirmationAuthService.getProfilePicture(username);
                confirmationAuthService.removePendingUser(username);
                confirmationAuthService.removeConfirmationCode(username);
                confirmationAuthService.removeProfilePicture(username);
                String profilePicturePath = saveProfilePicture(profilePictureTemporalPath, username);
                user.setProfilePicture(profilePicturePath);
                userRepository.save(user);
                return user;
            }
        }
        return null;
    }

    private String saveProfilePicture(String profilePicture, String username){
        String profilePicturePath = null;
        Path temporalMultipartFilePath = null;
        if (profilePicture != null && !profilePicture.isEmpty()){
            temporalMultipartFilePath = Paths.get(profilePicture);
        }
        if(temporalMultipartFilePath != null && Files.exists(temporalMultipartFilePath)){
            try {
                String originalFilename = temporalMultipartFilePath.getFileName().toString();
                String extension = "";
                if(originalFilename != null && originalFilename.contains(".")){
                    extension = originalFilename.substring(originalFilename.lastIndexOf("."));
                }
                if (!extension.equals(".jpg") && !extension.equals(".jpeg") && !extension.equals(".png") &&
                !extension.equals(".JPG") && !extension.equals(".JPEG") && !extension.equals(".PNG") && !extension.equals(".gif") && !extension.equals(".GIF")) {
                    throw new InvalidFileTypeException("Profile picture must be an image file with extension .jpg, .jpeg, .png, or .gif");
                }
                String filename = StringUtils.cleanPath(username + extension);
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
                Files.copy(temporalMultipartFilePath, filePath, StandardCopyOption.REPLACE_EXISTING);
                Files.delete(temporalMultipartFilePath);
                profilePicturePath = filePath.toString();
            } catch (IOException exception){
                throw new RuntimeException("Failed to upload profile picture", exception);
            }
        }
        else{
            String defaultProfilePicturePath = uploadFolderUsersPhotosPath + "/default.png";
            File defaultImageFile = new File(defaultProfilePicturePath);
            String extension = ".png";
            String filename = StringUtils.cleanPath(username + extension);
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
        String uploadUserFolder = uploadFolderUsersFoldersPath + username;
        Path uploadUserFolderPath = Paths.get(uploadUserFolder);
        if(!Files.exists(uploadUserFolderPath)){
            try {
                Files.createDirectories(uploadUserFolderPath);
            } catch (IOException e) {
                throw new RuntimeException("Failed to create directory for users folders", e);
            }
        }
        return profilePicturePath;
    }

    public void cancelRegistration(String username){
        User user = confirmationAuthService.getPendingUser(username);
        if(user != null){
            confirmationAuthService.removePendingUser(username);
            confirmationAuthService.removeConfirmationCode(username);
            confirmationAuthService.removeProfilePicture(username);
        }
    }

    public void cancelSignIn(String username){
        User user = confirmationAuthService.getPendingUser(username);
        if(user != null){
            confirmationAuthService.removePendingUser(username);
            confirmationAuthService.removeConfirmationCode(username);
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

    public User findUserByIdentifier(String identifier) {
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
        return user;
    }
}
