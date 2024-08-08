package com.vivafit.vivafit.authentification.services;

import com.vivafit.vivafit.authentification.entities.User;
import com.vivafit.vivafit.authentification.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

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
}
