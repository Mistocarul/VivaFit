package com.vivafit.vivafit.autentificare.entitati;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString

@Table(name = "utilizatori")
@Entity
public class Utilizator {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false, unique = true)
    private Integer id;

    @Column(name = "poza_profil", nullable = false, length = 255)
    private String pozaProfil;

    @Column(name = "nume_utilizator", nullable = false, unique = true, length = 25)
    private String numeUtilizator;

    @Column(name = "parola", nullable = false, length = 255)
    private String parola;

    @Column(name = "email", nullable = false, unique = true, length = 50)
    private String email;

    @Column(name = "numar_telefon", nullable = false, unique = true, length = 20)
    private String numarTelefon;

    @Column(name = "rol", nullable = false, length = 20)
    private String rol;

    @CreationTimestamp
    @Column(name = "data_creare", nullable = false, updatable = false)
    private Date dataCreare;

    @UpdateTimestamp
    @Column(name = "data_modificare", nullable = false)
    private Date dataModificare;
}
