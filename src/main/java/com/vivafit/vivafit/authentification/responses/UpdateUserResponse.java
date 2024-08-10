package com.vivafit.vivafit.authentification.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class UpdateUserResponse {
    private String message;
    private String token;
    private long expirationTime;
    private String username;
}
