package com.vivafit.vivafit.manage_calories.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Getter
@Setter

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "recipes")
@Entity
public class Recipe {
    @Id
    private Integer id;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "portions", length = 255)
    private int portions;

    @Column(name = "prep_time_minutes", nullable = false)
    private int prepTimeMinutes;

    @Column(name = "cook_time_minutes", nullable = false)
    private int cookTimeMinutes;

    @Column(name = "weight_after_cooking", nullable = false)
    private int weightAfterCooking;

    @Column(columnDefinition = "TEXT", length = 255, name = "preparation_instructions")
    private String preparationInstructions;

    @Column(columnDefinition = "TEXT", length = 255, name = "ingredients")
    private String ingredients;

    @Column(name = "calories_per_100g", nullable = false)
    private double caloriesPer100g;

    @Column(name = "protein_per_100g", nullable = false)
    private double proteinPer100g;

    @Column(name = "fat_per_100g", nullable = false)
    private double fatPer100g;

    @Column(name = "carbs_per_100g", nullable = false)
    private double carbsPer100g;

    @Column(name = "image_path", length = 255)
    private String imagePath;

    @Column(name = "created_by", nullable = false)
    private String createdBy;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

}
