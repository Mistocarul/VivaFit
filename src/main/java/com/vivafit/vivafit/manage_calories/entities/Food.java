package com.vivafit.vivafit.manage_calories.entities;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

@Table(name = "foods")
@Entity
public class Food {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false, unique = false)
    private Integer id;

    @Column(name = "barcode", nullable = true, unique = false, length = 30)
    private String barcode;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "calories_per_100g", nullable = false)
    private Double caloriesPer100g;

    @Column(name = "protein_per_100g", nullable = false)
    private Double proteinPer100g;

    @Column(name = "fat_per_100g", nullable = false)
    private Double fatPer100g;

    @Column(name = "carbs_per_100g", nullable = false)
    private Double carbsPer100g;

    @Column(name = "created_by", nullable = false, length = 50)
    private String createdBy;
}
