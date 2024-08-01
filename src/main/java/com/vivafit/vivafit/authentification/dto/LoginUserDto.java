package com.vivafit.vivafit.authentification.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class LoginUserDto {

    @NotEmpty(message = "Username or email is required")
    private String identifier;

    @NotEmpty(message = "Password is required")
    private String password;
}
