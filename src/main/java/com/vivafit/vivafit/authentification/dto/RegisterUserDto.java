package com.vivafit.vivafit.authentification.dto;

import com.vivafit.vivafit.authentification.entities.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class RegisterUserDto {
    private MultipartFile profilePicture;

    @NotEmpty(message = "Username is required")
    @Size(min = 4, max = 24, message = "Username must be between 4 and 20 characters")
    @Pattern(regexp = "^[^@]*$", message = "Username must not contain '@'")
    private String username;

    @NotEmpty(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*(),.?\":{}|<>]).{8,}$",
            message = "Password must contain at least one uppercase letter, one digit, and one special character"
    )
    private String password;

    @NotEmpty(message = "Email is required")
    @Email(message = "The email address is invalid.", flags = { Pattern.Flag.CASE_INSENSITIVE })
    private String email;

    @NotEmpty(message = "Phone number is required")
    @Pattern(regexp = "\\+.*", message = "Phone number must start with '+' sign")
    @Size(min = 6, max = 15, message = "Phone number must be between 6 and 15 characters")
    private String phoneNumber;

    @NotEmpty(message = "Role is required")
    @Pattern(regexp = "ADMIN|USER|COACH|NUTRITIONIST", message = "Role must be one of ADMIN, USER, COACH, NUTRITIONIST")
    private String role;

    public User toUser(String profilePicturePath) {
        User user = new User();
        user.setProfilePicture(profilePicturePath);
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);
        user.setPhoneNumber(phoneNumber);
        user.setRole(role);
        return user;
    }

}
