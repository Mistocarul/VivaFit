package com.vivafit.vivafit.manage_calories.entities;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "meal")
@Table(name = "meal_foods")
@Entity
public class MealFood {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false, unique = true)
    private Integer id;


    @Column(name = "quantity", nullable = false)
    private Double quantity;

    @Column(name = "calories", nullable = false)
    private Double calories;

    @Column(name = "protein", nullable = false)
    private Double protein;

    @Column(name = "fat", nullable = false)
    private Double fat;

    @Column(name = "carbs", nullable = false)
    private Double carbs;

    @ManyToOne
    @JoinColumn(name = "meal_id")
    private Meal meal;

    @ManyToOne
    @JoinColumn(name = "food_id")
    private Food food;
}
