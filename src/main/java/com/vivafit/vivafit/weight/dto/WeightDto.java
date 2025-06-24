package com.vivafit.vivafit.weight.dto;

import lombok.*;

import java.time.LocalDate;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WeightDto {
    private Integer id;
    private LocalDate date;
    private Double value;
    private Integer userId;
}
