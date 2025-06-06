package com.vivafit.vivafit.manage_calories.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

@Table(name = "food_favorites")
@Entity
public class FoodFavorite {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false, unique = false)
    private Integer id;

    @Column(name = "user_id", nullable = false, updatable = false, unique = false)
    private Integer userId;

    @Column(name = "food_id", nullable = false, updatable = false, unique = false)
    private Integer foodId;
}
