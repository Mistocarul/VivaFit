package com.vivafit.vivafit.specialist.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SpecialistDto {

    private Integer id;

    private MultipartFile profilePicture;

    private String profilePictureUrl;

    private Integer userId;

    private Integer numberOfVisits;

    @Size(max = 50, message = "Name must not exceed 50 characters")
    private String name;

    @Size(max = 50, message = "Profession must not exceed 50 characters")
    private String profession;

    @Size(max = 255, message = "Introduction must not exceed 255 characters")
    private String introduction;

    @Size(max = 255, message = "Facebook link must not exceed 255 characters")
    private String facebookLink;

    @Size(max = 255, message = "Instagram link must not exceed 255 characters")
    private String instagramLink;

    @Size(max = 255, message = "YouTube link must not exceed 255 characters")
    private String youtubeLink;

    @Email(message = "Email must be valid")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    private String email;

    @Size(max = 15, message = "Phone number must not exceed 15 characters")
    private String phoneNumber;

    @Size(max = 255, message = "Address must not exceed 255 characters")
    private String address;

    @Size(max = 50, message = "City must not exceed 50 characters")
    private String city;

    @Size(max = 50, message = "State must not exceed 50 characters")
    private String state;

    @Size(max = 500, message = "About me must not exceed 500 characters")
    private String aboutMe;

    @Size(max = 500, message = "Experience must not exceed 500 characters")
    private String experience;

    @Size(max = 500, message = "Specializations must not exceed 500 characters")
    private String specializations;

    @Size(max = 500, message = "Programs must not exceed 500 characters")
    private String programs;

    @Size(max = 500, message = "Another details must not exceed 500 characters")
    private String anotherDetails;

}