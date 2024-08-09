package com.vivafit.vivafit.authentification.services;

import com.vivafit.vivafit.authentification.dto.UpdateUserInformationsDto;
import com.vivafit.vivafit.authentification.entities.User;
import com.vivafit.vivafit.authentification.exceptions.DataAlreadyExistsException;
import com.vivafit.vivafit.authentification.exceptions.InvalidFileTypeException;
import com.vivafit.vivafit.authentification.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

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
        String userFolder = uploadFolderUsersFoldersPath + user.getUsername();
        File folder = new File(userFolder);
        try {
            deleteRecursive(folder);
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete user folder", e);
        }
        userRepository.delete(user);
    }

    private void deleteRecursive(File folder) throws IOException {
        File[] files = folder.listFiles();
        if(files != null) {
            for(File file : files) {
                if (file.isDirectory()) {
                    deleteRecursive(file);
                } else {
                    Files.delete(file.toPath());
                }
            }
        }
        Files.delete(folder.toPath());
    }

    public User updateUserInformation(UpdateUserInformationsDto updateUserInformationsDto, User currentUser){
        if(!passwordEncoder.matches(updateUserInformationsDto.getCurrentPassword(), currentUser.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }
        if(updateUserInformationsDto.getNewUsername() != null && !updateUserInformationsDto.getNewUsername().isEmpty()){
            if(userRepository.existsByUsername(updateUserInformationsDto.getNewUsername())){
                throw new DataAlreadyExistsException("Username is already taken");
            }
            currentUser.setUsername(updateUserInformationsDto.getNewUsername());
            String oldUserFolder = uploadFolderUsersFoldersPath + currentUser.getUsername();
            String newUserFolder = uploadFolderUsersFoldersPath + updateUserInformationsDto.getNewUsername();
            Path oldUserFolderPath = Paths.get(oldUserFolder);
            Path newUserFolderPath = Paths.get(newUserFolder);
            try {
                Files.createDirectories(newUserFolderPath);
            } catch (IOException e) {
                throw new RuntimeException("Failed to create new user folder", e);
            }
            try {
                Files.move(oldUserFolderPath, newUserFolderPath, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                throw new RuntimeException("Failed to move user folder", e);
            }
            if(Files.exists(oldUserFolderPath)){
                try {
                    Files.delete(oldUserFolderPath);
                } catch (IOException e) {
                    throw new RuntimeException("Failed to delete old user folder", e);
                }
            }
            String currentProfilePicture = currentUser.getProfilePicture();
            Path currentProfilePicturePath = Paths.get(currentProfilePicture);
            if(currentProfilePicture != null && !currentProfilePicture.isEmpty()){
                String newProfilePicture = currentProfilePicture.replace(currentUser.getUsername(), updateUserInformationsDto.getNewUsername());
                Path newProfilePicturePath = Paths.get(newProfilePicture);
                try {
                    Files.move(currentProfilePicturePath, newProfilePicturePath, StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    throw new RuntimeException("Failed to move profile picture", e);
                }
                if(Files.exists(currentProfilePicturePath)){
                    try {
                        Files.delete(currentProfilePicturePath);
                    } catch (IOException e) {
                        throw new RuntimeException("Failed to delete old profile picture", e);
                    }
                }
                currentUser.setProfilePicture(newProfilePicture);
            }
        }
        if (updateUserInformationsDto.getNewPassword() != null && !updateUserInformationsDto.getNewPassword().isEmpty()) {
            currentUser.setPassword(passwordEncoder.encode(updateUserInformationsDto.getNewPassword()));
        }
        if (updateUserInformationsDto.getNewEmail() != null && !updateUserInformationsDto.getNewEmail().isEmpty()) {
            if (userRepository.existsByEmail(updateUserInformationsDto.getNewEmail())) {
                throw new DataAlreadyExistsException("Email is already registered.");
            }
            currentUser.setEmail(updateUserInformationsDto.getNewEmail());
        }
        if (updateUserInformationsDto.getNewPhoneNumber() != null && !updateUserInformationsDto.getNewPhoneNumber().isEmpty()) {
            if (userRepository.existsByPhoneNumber(updateUserInformationsDto.getNewPhoneNumber())) {
                throw new DataAlreadyExistsException("Phone number is already registered.");
            }
            currentUser.setPhoneNumber(updateUserInformationsDto.getNewPhoneNumber());
        }
        if (updateUserInformationsDto.getNewProfilePicture() != null && !updateUserInformationsDto.getNewProfilePicture().isEmpty()) {
            try {
                String originalFilename = updateUserInformationsDto.getNewProfilePicture().getOriginalFilename();
                String extension = "";
                if (originalFilename != null && originalFilename.contains(".")) {
                    extension = originalFilename.substring(originalFilename.lastIndexOf("."));
                }
                if (!extension.equals(".jpg") && !extension.equals(".jpeg") && !extension.equals(".png") &&
                        !extension.equals(".JPG") && !extension.equals(".JPEG") && !extension.equals(".PNG") && !extension.equals(".gif") && !extension.equals(".GIF")) {
                    throw new InvalidFileTypeException("Profile picture must be an image file with extension .jpg, .jpeg, .png, or .gif");
                }

                String usernameToUse = currentUser.getUsername();
                if (updateUserInformationsDto.getNewUsername() != null && !updateUserInformationsDto.getNewUsername().isEmpty()) {
                    usernameToUse = updateUserInformationsDto.getNewUsername();
                }
                String filename = StringUtils.cleanPath(usernameToUse + extension);
                Path uploadFolderPath = Paths.get(uploadFolderUsersPhotosPath);
                if (!Files.exists(uploadFolderPath)) {
                    try {
                        Files.createDirectories(uploadFolderPath);
                    } catch (IOException e) {
                        throw new RuntimeException("Failed to create directory for users photos", e);
                    }
                }
                Path filePath = uploadFolderPath.resolve(filename);
                updateUserInformationsDto.getNewProfilePicture().transferTo(filePath.toFile());
                String oldProfilePicture = currentUser.getProfilePicture();
                if (oldProfilePicture != null && !oldProfilePicture.equals(filePath.toString())) {
                    Files.deleteIfExists(Paths.get(oldProfilePicture));
                }
                currentUser.setProfilePicture(filePath.toString());
            } catch (IOException e) {
                throw new RuntimeException("Failed to upload profile picture", e);
            }
        }
        return userRepository.save(currentUser);
    }
}
