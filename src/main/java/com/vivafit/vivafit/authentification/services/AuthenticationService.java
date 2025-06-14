package com.vivafit.vivafit.authentification.services;

import com.vivafit.vivafit.authentification.dto.LoginUserDto;
import com.vivafit.vivafit.authentification.dto.RegisterUserDto;
import com.vivafit.vivafit.authentification.entities.*;
import com.vivafit.vivafit.authentification.exceptions.DataAlreadyExistsException;
import com.vivafit.vivafit.authentification.exceptions.InvalidFileTypeException;
import com.vivafit.vivafit.authentification.exceptions.InvalidTokenException;
import com.vivafit.vivafit.authentification.exceptions.PasswordsDoNotMatchException;
import com.vivafit.vivafit.authentification.repositories.PasswordResetTokenRepository;
import com.vivafit.vivafit.authentification.repositories.UserRepository;
import com.vivafit.vivafit.manage_calories.entities.BMRDetails;
import com.vivafit.vivafit.manage_calories.repositories.BMRDetailsRepository;
import com.vivafit.vivafit.manage_calories.services.BMRDetailsService;
import com.vivafit.vivafit.manage_calories.services.MealTypeService;
import com.vivafit.vivafit.specialist.services.SpecialistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
import java.util.Objects;
import java.util.UUID;

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
    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;
    @Autowired
    private EncryptionDataService encryptionDataService;
    @Autowired
    private BMRDetailsService bmrDetailsService;
    @Autowired
    private MealTypeService mealTypeService;
    @Autowired
    private SpecialistService specialistService;

    @Value("${upload.folder.users-photos.path}")
    private String uploadFolderUsersPhotosPath;
    @Value("${upload.folder.users-folders.path}")
    private String uploadFolderUsersFoldersPath;
    @Value("${upload.temporal.multipart.folder}")
    private String uploadTemporalMultipartFolder;
    @Value("${server.link.reset-password}")
    private String resetPasswordLink;

    public User registerUser(RegisterUserDto registerUserDto) {
        if (registerUserDto.getConfirmPassword() == null || !registerUserDto.getPassword().equals(registerUserDto.getConfirmPassword())) {
            throw new PasswordsDoNotMatchException("Passwords do not match");
        }
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
        user.setCreatedWith("OWN_METHOD");

        PendingSignUpUser pendingSignUpUser = toPendingUser(user);

        confirmationAuthService.removePendingSignUpUser(pendingSignUpUser);
        confirmationAuthService.removeConfirmationCode(pendingSignUpUser.getUsername());

        emailService.setUser(user);
        emailService.setWhatSituation(0);
        emailService.sendEmail();

        ConfirmationCode confirmationCode = new ConfirmationCode();
        confirmationCode.setCode(emailService.getCode());
        confirmationCode.setCreationTime(LocalDateTime.now());
        confirmationAuthService.addConfirmationCode(user.getUsername(), confirmationCode);

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

                System.out.println(profilePicture);

                profilePicture.transferTo(filePath.toFile());
                String temporalMultipartFilePath = filePath.toString();
                pendingSignUpUser.setProfilePicture(temporalMultipartFilePath);

            } catch (IOException e) {
                throw new RuntimeException("Failed to upload profile picture", e);
            }
        }
        confirmationAuthService.addPendingSignUpUser(pendingSignUpUser);
        return user;
    }

    public void sendEmailForNewBrowser(String username) {
        User user = userRepository
                .findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        emailService.setUser(user);
        emailService.setWhatSituation(0);
        emailService.sendEmail();

        ConfirmationCode confirmationCode = new ConfirmationCode();
        confirmationCode.setCode(emailService.getCode());
        confirmationCode.setCreationTime(LocalDateTime.now());
        confirmationAuthService.addConfirmationCode(user.getUsername(), confirmationCode);
    }

    public User resendEmail(String username){
        PendingSignUpUser pendingSignUpUser = confirmationAuthService.getPendingSignUpUser(username);
        if(pendingSignUpUser != null){
            confirmationAuthService.removeConfirmationCode(username);

            User user = toUser(pendingSignUpUser);

            emailService.setUser(user);
            emailService.setWhatSituation(0);
            emailService.sendEmail();

            ConfirmationCode confirmationCode = new ConfirmationCode();
            confirmationCode.setCode(emailService.getCode());
            confirmationCode.setCreationTime(LocalDateTime.now());
            confirmationAuthService.addConfirmationCode(username, confirmationCode);

            return user;
        }
        return null;
    }

    public User confirmSignIn(String username, int code) {
        ConfirmationCode confirmationCode = confirmationAuthService.getConfirmationCode(username);
        if (confirmationCode == null) {
            throw new RuntimeException("Confirmation code not found");
        }
        LocalDateTime now = LocalDateTime.now();
        if (Duration.between(confirmationCode.getCreationTime(), now).toMinutes() > 30) {
            confirmationAuthService.removeConfirmationCode(username);
            PendingSignInUser pendingSignInUser = confirmationAuthService.getPendingSignInUser(username);
            confirmationAuthService.removePendingSignInUser(pendingSignInUser);
            throw new RuntimeException("Confirmation code has expired");
        }
        Integer codeConfirmation = confirmationCode.getCode();
        if (codeConfirmation != null && codeConfirmation == code) {
            PendingSignInUser pendingSignInUser = confirmationAuthService.getPendingSignInUser(username);
            if (pendingSignInUser != null) {

                confirmationAuthService.removePendingSignInUser(pendingSignInUser);
                confirmationAuthService.removeConfirmationCode(username);

                User user = userRepository
                        .findByUsername(username)
                        .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

                return user;
            }
        }
        return null;
    }

    public User confirmUser(String username, int code){
        ConfirmationCode confirmationCode = confirmationAuthService.getConfirmationCode(username);
        if(confirmationCode == null){
            throw new RuntimeException("Confirmation code not found");
        }
        LocalDateTime now = LocalDateTime.now();
        if (Duration.between(confirmationCode.getCreationTime(), now).toMinutes() > 30) {
            PendingSignUpUser pendingSignUpUser = confirmationAuthService.getPendingSignUpUser(username);
            confirmationAuthService.removePendingSignUpUser(pendingSignUpUser);
            throw new RuntimeException("Confirmation code has expired");
        }
        Integer codeConfirmation = confirmationCode.getCode();
        if(codeConfirmation != null && codeConfirmation == code){
            PendingSignUpUser pendingSignUpUser = confirmationAuthService.getPendingSignUpUser(username);
            if(pendingSignUpUser != null){
                String profilePictureTemporalPath = pendingSignUpUser.getProfilePicture();

                confirmationAuthService.removePendingSignUpUser(pendingSignUpUser);
                confirmationAuthService.removeConfirmationCode(username);

                String profilePicturePath = saveProfilePicture(profilePictureTemporalPath, username);
                pendingSignUpUser.setProfilePicture(profilePicturePath);

                User user = toUser(pendingSignUpUser);
                userRepository.save(user);
                user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
                if (user.getRole().equals("USER")) {
                    bmrDetailsService.initializeBMRDetails(user);
                    mealTypeService.initializeMealType(user);
                }
                if (user.getRole().equals("NUTRITIONIST") || user.getRole().equals("COACH")) {
                    specialistService.initializeProfile(user.getId());
                }
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
        PendingSignUpUser pendingSignUpUser = confirmationAuthService.getPendingSignUpUser(username);
        if(pendingSignUpUser != null){
            confirmationAuthService.removePendingSignUpUser(pendingSignUpUser);
            confirmationAuthService.removeConfirmationCode(username);
        }
    }

    public void cancelSignIn(String username){
        PendingSignInUser pendingSignInUser = confirmationAuthService.getPendingSignInUser(username);
        if(pendingSignInUser != null){
            confirmationAuthService.removePendingSignInUser(pendingSignInUser);
            confirmationAuthService.removeConfirmationCode(username);
        }
    }

    public User loginUser(LoginUserDto loginUserDto) {
        String identifier = loginUserDto.getIdentifier();
        User user = null;
        String password = loginUserDto.getPassword();
        if (encryptionDataService.isEncrypted(password)) {
            try {
                password = encryptionDataService.decrypt(password);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("Failed to decrypt password");
            }
        }
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
        if (user.getCreatedWith() != null && !user.getCreatedWith().equals("OWN_METHOD")){
            throw new RuntimeException("User was created with " + user.getCreatedWith());
        }
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        user.getUsername(),
                        password
                )
        );
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        user.getUsername(),
                        password
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        return user;
    }

    public boolean isPasswordCorrect(String username, String password){
        User user = userRepository
                .findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
        return passwordEncoder.matches(password, user.getPassword());
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

    public void forgotPassword(String email){
        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        if (!Objects.equals(user.getRole(), "USER")){
            throw new UsernameNotFoundException("User not found with email: " + email);
        }
        passwordResetTokenRepository.findByUser(user).ifPresent(passwordResetTokenRepository::delete);
        String token = UUID.randomUUID().toString();
        LocalDateTime expiryDate = LocalDateTime.now().plusMinutes(30);

        PasswordResetToken passwordResetToken = new PasswordResetToken();
        passwordResetToken.setToken(token);
        passwordResetToken.setUser(user);
        passwordResetToken.setExpiryDate(expiryDate);
        passwordResetTokenRepository.save(passwordResetToken);

        emailService.setUser(user);
        emailService.setWhatSituation(1);
        emailService.setResetLink(resetPasswordLink + token);
        emailService.sendEmail();
    }

    public void resetPassword(String token, String newPassword, String confirmNewPassword){
        PasswordResetToken passwordResetToken = passwordResetTokenRepository
                .findByToken(token)
                .orElseThrow(() -> new InvalidTokenException("Invalid token"));
        if(passwordResetToken.isExpired()){
            passwordResetTokenRepository.delete(passwordResetToken);
            throw new InvalidTokenException("Token has expired");
        }
        if(!newPassword.equals(confirmNewPassword)){
            throw new PasswordsDoNotMatchException("Passwords do not match");
            }
        User user = passwordResetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        passwordResetTokenRepository.delete(passwordResetToken);
    }

    public void sendNewSignInAlertEmail(String username, String ipAdress, String userAgent, String device){
        User user = userRepository
                .findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        emailService.setUser(user);
        emailService.setWhatSituation(2);
        emailService.setIpAdress(ipAdress);
        emailService.setUserAgent(userAgent);
        emailService.setDevice(device);
        emailService.sendEmail();
    }


    public PendingSignUpUser toPendingUser(User user){
        PendingSignUpUser pendingSignUpUser = new PendingSignUpUser();
        if(user.getUsername() != null){
            pendingSignUpUser.setUsername(user.getUsername());
        }
        if(user.getPassword() != null){
            pendingSignUpUser.setPassword(user.getPassword());
        }
        if(user.getEmail() != null){
            pendingSignUpUser.setEmail(user.getEmail());
        }
        if(user.getPhoneNumber() != null){
            pendingSignUpUser.setPhoneNumber(user.getPhoneNumber());
        }
        if(user.getRole() != null){
            pendingSignUpUser.setRole(user.getRole());
        }
        if(user.getProfilePicture() != null){
            pendingSignUpUser.setProfilePicture(user.getProfilePicture());
        }
        if (user.getCreatedWith() != null){
            pendingSignUpUser.setCreatedWith(user.getCreatedWith());
        }
        return pendingSignUpUser;
    }

    public User toUser (PendingSignUpUser pendingSignUpUser){
        User user = new User();
        if(pendingSignUpUser.getUsername() != null){
            user.setUsername(pendingSignUpUser.getUsername());
        }
        if(pendingSignUpUser.getPassword() != null){
            user.setPassword(pendingSignUpUser.getPassword());
        }
        if(pendingSignUpUser.getEmail() != null){
            user.setEmail(pendingSignUpUser.getEmail());
        }
        if(pendingSignUpUser.getPhoneNumber() != null){
            user.setPhoneNumber(pendingSignUpUser.getPhoneNumber());
        }
        if(pendingSignUpUser.getRole() != null){
            user.setRole(pendingSignUpUser.getRole());
        }
        if(pendingSignUpUser.getProfilePicture() != null){
            user.setProfilePicture(pendingSignUpUser.getProfilePicture());
        }
        if(pendingSignUpUser.getCreatedWith() != null){
            user.setCreatedWith(pendingSignUpUser.getCreatedWith());
        }
        return user;
    }
}
