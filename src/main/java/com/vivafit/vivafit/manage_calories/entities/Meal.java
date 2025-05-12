package com.vivafit.vivafit.manage_calories.entities;

import com.vivafit.vivafit.authentification.entities.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Getter
@Setter

@Builder
@ToString(exclude = {"user", "mealFoods"})
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "meals")
@Entity
public class Meal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false, unique = true)
    private Integer id;

    @Column(name = "meal_name", nullable = false, length = 30)
    private String mealType;

    @Column(name = "meal_date", nullable = false)
    private LocalDate date;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "meal", cascade = CascadeType.ALL)
    private List<MealFood> mealFoods;
}
