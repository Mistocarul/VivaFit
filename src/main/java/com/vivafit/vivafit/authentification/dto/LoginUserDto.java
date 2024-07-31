package com.vivafit.vivafit.authentification.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class LoginUserDto {

    @NotEmpty(message = "Username is required")
    private String username;

    @NotEmpty(message = "Password is required")
    private String password;
}
