package com.vivafit.vivafit.authentification.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class DeleteConnectionDetailDto {
    @NotEmpty(message = "Id is required")
    private String id;
}
