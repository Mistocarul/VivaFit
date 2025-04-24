package com.vivafit.vivafit.ai.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class ChatRequestDto {
    @NotEmpty(message = "Prompt is required")
    private String prompt;

    @NotEmpty(message = "Category is required")
    @Pattern(regexp = "Discutie|AnalizaMancare", message = "Category must be either 'Discutie' or 'AnalizaMancare'")
    private String category;

    private String foodDate;

    private String foodName;
}
