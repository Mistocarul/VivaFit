package com.vivafit.vivafit.manage_calories.entities;

import com.vivafit.vivafit.authentification.entities.User;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString

@Table(name = "bmr_details")
@Entity
public class BMRDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false, unique = true)
    private Integer id;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    @Column(name = "gender", nullable = true, length = 10)
    private String gender;

    @Column(name = "age", nullable = true, length = 3)
    private Integer age;

    @Column(name = "height_in_cm", nullable = true, length = 3)
    private Double height;

    @Column(name = "weight_in_kg", nullable = true, length = 3)
    private Double weight;

    @Column(name = "activity_level_per_week", nullable = true, length = 20)
    private String activityLevel;

    @Column(name = "calorie_difference", nullable = true, length = 20)
    private Integer calorieDifference;

    @Column(name = "objective", nullable = true, length = 20)
    private String objective;

    @Column(name = "number_of_calories", nullable = true, length = 20)
    private Integer numberOfCalories;

    @Column(name = "grams_of_proteins", nullable = true, length = 20)
    private Integer gramsOfProteins;

    @Column(name = "grams_of_fats", nullable = true, length = 20)
    private Integer gramsOfFats;

    @Column(name = "grams_of_carbs", nullable = true, length = 20)
    private Integer gramsOfCarbs;
}
