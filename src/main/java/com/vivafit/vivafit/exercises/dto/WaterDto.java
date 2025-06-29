package com.vivafit.vivafit.exercises.dto;

import lombok.*;

import java.time.LocalDate;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WaterDto {
    private Integer id;
    private Integer userId;
    private Double amount;
    private LocalDate date;
}
