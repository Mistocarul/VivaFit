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
public class UpdateUserInformationsDto {
    private MultipartFile newProfilePicture;

    @Size(min = 4, max = 24, message = "Username must be between 4 and 20 characters")
    @Pattern(regexp = "^[^@]*$", message = "Username must not contain '@'")
    private String newUsername;

    @NotEmpty(message = "Password is required")
    private String currentPassword;

    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*(),.?\":{}|<>]).{8,}$",
            message = "Password must contain at least one uppercase letter, one digit, and one special character"
    )
    private String newPassword;

    @Pattern(regexp = "\\+.*", message = "Phone number must start with '+' sign")
    @Size(min = 6, max = 15, message = "Phone number must be between 6 and 15 characters")
    private String newPhoneNumber;
}
