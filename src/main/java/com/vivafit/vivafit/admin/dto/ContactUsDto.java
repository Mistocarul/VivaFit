package com.vivafit.vivafit.admin.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor

public class ContactUsDto {
    @NotEmpty(message = "First name is required")
    @Size(min = 1, max = 250,message = "First name must be at least 2 characters long")
    @Pattern(regexp = "^[A-Za-zăâîșțĂÂÎȘȚ]+$", message = "First name must contain only letters")
    private String firstName;

    @NotEmpty(message = "Last name is required")
    @Size(min = 1, max = 250,message = "Last name must be at least 2 characters long")
    @Pattern(regexp = "^[A-Za-zăâîșțĂÂÎȘȚ]+$", message = "Last name must contain only letters")
    private String lastName;

    @NotEmpty(message = "Email is required")
    @Size(min = 1, max = 250,message = "Email must be at least 2 characters long")
    @Email(message = "The email address is invalid.", flags = { Pattern.Flag.CASE_INSENSITIVE })
    @Pattern(regexp = "^(.+)@(.+)$", message = "The email address is invalid.")
    private String email;

    @NotEmpty(message = "Phone number is required")
    @Size(min = 1, max = 250,message = "Phone number must be at least 2 characters long")
    @Pattern(regexp = "\\+.*", message = "Phone number must start with '+' sign")
    private String phoneNumber;

    @NotEmpty(message = "Message is required")
    @Size(min = 1, max = 250,message = "Message must be at least 2 characters long")
    private String message;
}
