package com.vivafit.vivafit.authentification.responses;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponse {
    private String token;
    private long expirationTime;
    private String username;
}
