package com.vivafit.vivafit.authentification.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Setter
public class ConfirmationCode {
    private int code;
    private LocalDateTime creationTime;
}
