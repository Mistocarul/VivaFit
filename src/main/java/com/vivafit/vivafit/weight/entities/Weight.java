package com.vivafit.vivafit.weight.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

@Table(name = "weights")
@Entity
public class Weight {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false, unique = true)
    private Integer id;

    @Column(name = "weight_date", nullable = false)
    private LocalDate date;

    @Column(name = "weight_value", nullable = false)
    private Double value;

    @Column(name = "user_id", nullable = false)
    private Integer userId;
}
