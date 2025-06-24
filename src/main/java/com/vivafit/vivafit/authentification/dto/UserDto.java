package com.vivafit.vivafit.authentification.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class UserDto {
    private MultipartFile newProfilePicture;

    private String profilePicturePath;

    private String username;

    private String email;

    private String currentPassword;

    private String newPassword;

    private String phoneNumber;

    private String token;

    private String createdWith;
}
