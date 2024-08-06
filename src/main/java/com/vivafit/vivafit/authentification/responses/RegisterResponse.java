package com.vivafit.vivafit.authentification.responses;

import com.vivafit.vivafit.authentification.entities.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterResponse {
    private String message;
    private User user;
}
