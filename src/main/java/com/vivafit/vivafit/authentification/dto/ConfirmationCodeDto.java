package com.vivafit.vivafit.authentification.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class ConfirmationCodeDto {
    @NotEmpty(message = "Username is required")
    private String username;

    @NotNull(message = "Code is required")
    private Integer code;
}
