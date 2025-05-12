package com.vivafit.vivafit.manage_calories.entities;

import com.vivafit.vivafit.authentification.entities.User;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

@Builder
@ToString(exclude = "user")
@Table(name = "meal_types")
@Entity
public class MealType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false, unique = true)
    private Integer id;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    @Column(name = "meal_type1", nullable = true, length = 30)
    private String mealType1;

    @Column(name = "meal_type2", nullable = true, length = 30)
    private String mealType2;

    @Column(name = "meal_type3", nullable = true, length = 30)
    private String mealType3;

    @Column(name = "meal_type4", nullable = true, length = 30)
    private String mealType4;

    @Column(name = "meal_type5", nullable = true, length = 30)
    private String mealType5;
}
