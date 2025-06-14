package com.vivafit.vivafit.specialist.entities;

import jakarta.persistence.*;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

@Table(name = "specialists")
@Entity
public class Specialist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false, unique = true)
    private Integer id;

    @Column(name = "profile_picture", nullable = false, length = 255)
    private String profilePicture;

    @Column(name = "user_id", nullable = false, updatable = false, unique = true)
    private Integer userId;

    @Column(name = "name", nullable = true, length = 50)
    private String name;

    @Column(name = "profession", nullable = true, length = 50)
    private String profession;

    @Column(columnDefinition = "TEXT", length = 255, name = "introduction", nullable = true)
    private String introduction;

    @Column(name = "facebook_link", nullable = true, length = 255)
    private String facebookLink;

    @Column(name = "instagram_link", nullable = true, length = 255)
    private String instagramLink;

    @Column(name = "youtube_link", nullable = true, length = 255)
    private String youtubeLink;

    @Column(name = "email", nullable = true, length = 100)
    private String email;

    @Column(name = "phone_number", nullable = true, length = 15)
    private String phoneNumber;

    @Column(name = "address", nullable = true, length = 255)
    private String address;

    @Column(name = "city", nullable = true, length = 50)
    private String city;

    @Column(name = "state", nullable = true, length = 50)
    private String state;

    @Column(columnDefinition = "TEXT", length = 500, name = "about_me")
    private String aboutMe;

    @Column(columnDefinition = "TEXT", length = 500, name = "experience")
    private String experience;

    @Column(columnDefinition = "TEXT", length = 500, name = "specializations")
    private String specializations;

    @Column(columnDefinition = "TEXT", length = 500, name = "programs")
    private String programs;

    @Column(columnDefinition = "TEXT", length = 500, name = "another_details")
    private String anotherDetails;

    @Column(name = "number_of_visits", nullable = true)
    private Integer numberOfVisits;
}
