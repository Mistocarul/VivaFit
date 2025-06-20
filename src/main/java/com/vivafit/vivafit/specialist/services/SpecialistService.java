package com.vivafit.vivafit.specialist.services;

import com.vivafit.vivafit.authentification.entities.User;
import com.vivafit.vivafit.authentification.repositories.UserRepository;
import com.vivafit.vivafit.specialist.dto.SpecialistDto;
import com.vivafit.vivafit.specialist.entities.Specialist;
import com.vivafit.vivafit.specialist.repositories.SpecialistRepository;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Getter
@Setter
@Service
public class SpecialistService {
    @Autowired
    private SpecialistRepository specialistRepository;
    @Autowired
    private UserRepository userRepository;

    @Value("${upload.folder.users-photos.path}")
    private String basePath;

    public SpecialistDto updateProfile(SpecialistDto specialistDto) throws IOException {
        Specialist existingSpecialist = specialistRepository.findByUserId(specialistDto.getUserId())
                .orElseThrow(() -> new RuntimeException("Specialist not found for user ID: " + specialistDto.getUserId()));

        if (specialistDto.getProfilePicture() != null && !specialistDto.getProfilePicture().isEmpty()) {
            Path filePath = Paths.get(existingSpecialist.getProfilePicture());
            Files.copy(specialistDto.getProfilePicture().getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        }

        existingSpecialist.setName(specialistDto.getName());
        existingSpecialist.setProfession(specialistDto.getProfession());
        existingSpecialist.setIntroduction(specialistDto.getIntroduction());
        existingSpecialist.setFacebookLink(specialistDto.getFacebookLink());
        existingSpecialist.setInstagramLink(specialistDto.getInstagramLink());
        existingSpecialist.setYoutubeLink(specialistDto.getYoutubeLink());
        existingSpecialist.setEmail(specialistDto.getEmail());
        existingSpecialist.setPhoneNumber(specialistDto.getPhoneNumber());
        existingSpecialist.setAddress(specialistDto.getAddress());
        existingSpecialist.setCity(specialistDto.getCity());
        existingSpecialist.setState(specialistDto.getState());
        existingSpecialist.setAboutMe(specialistDto.getAboutMe());
        existingSpecialist.setExperience(specialistDto.getExperience());
        existingSpecialist.setSpecializations(specialistDto.getSpecializations());
        existingSpecialist.setPrograms(specialistDto.getPrograms());
        existingSpecialist.setAnotherDetails(specialistDto.getAnotherDetails());

        existingSpecialist = specialistRepository.save(existingSpecialist);

        Specialist finalExistingSpecialist = existingSpecialist;
        User user = userRepository.findById(existingSpecialist.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + finalExistingSpecialist.getUserId()));
        user.setProfilePicture(existingSpecialist.getProfilePicture());
        user.setPhoneNumber(existingSpecialist.getPhoneNumber());
        userRepository.save(user);

        return mapToDto(existingSpecialist);
    }

    public SpecialistDto getProfile(Integer userId) {
        Specialist specialist = specialistRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Specialist not found for user ID: " + userId));
        return mapToDto(specialist);
    }

    private Specialist mapToEntity(SpecialistDto dto) {
        return Specialist.builder()
                .id(dto.getId())
                .profilePicture(dto.getProfilePictureUrl())
                .userId(dto.getUserId())
                .name(dto.getName())
                .numberOfVisits(dto.getNumberOfVisits())
                .profession(dto.getProfession())
                .introduction(dto.getIntroduction())
                .facebookLink(dto.getFacebookLink())
                .instagramLink(dto.getInstagramLink())
                .youtubeLink(dto.getYoutubeLink())
                .email(dto.getEmail())
                .phoneNumber(dto.getPhoneNumber())
                .address(dto.getAddress())
                .city(dto.getCity())
                .state(dto.getState())
                .aboutMe(dto.getAboutMe())
                .experience(dto.getExperience())
                .specializations(dto.getSpecializations())
                .programs(dto.getPrograms())
                .anotherDetails(dto.getAnotherDetails())
                .build();
    }

    private SpecialistDto mapToDto(Specialist entity) {
        return new SpecialistDto(
                entity.getId(),
                null,
                entity.getProfilePicture(),
                entity.getUserId(),
                entity.getNumberOfVisits(),
                entity.getName(),
                entity.getProfession(),
                entity.getIntroduction(),
                entity.getFacebookLink(),
                entity.getInstagramLink(),
                entity.getYoutubeLink(),
                entity.getEmail(),
                entity.getPhoneNumber(),
                entity.getAddress(),
                entity.getCity(),
                entity.getState(),
                entity.getAboutMe(),
                entity.getExperience(),
                entity.getSpecializations(),
                entity.getPrograms(),
                entity.getAnotherDetails()
        );
    }

    public void initializeProfile(Integer userId) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        Specialist updatedSpecialist = Specialist.builder()
                .id(null)
                .userId(existingUser.getId())
                .profilePicture(existingUser.getProfilePicture())
                .name(null)
                .profession(null)
                .introduction(null)
                .facebookLink(null)
                .instagramLink(null)
                .youtubeLink(null)
                .email(existingUser.getEmail())
                .phoneNumber(existingUser.getPhoneNumber())
                .address(null)
                .city(null)
                .state(null)
                .aboutMe(null)
                .experience(null)
                .specializations(null)
                .programs(null)
                .anotherDetails(null)
                .numberOfVisits(0)
                .build();

        updatedSpecialist = specialistRepository.save(updatedSpecialist);
    }

    public SpecialistDto addVisitProfile(SpecialistDto specialistDto) {
        Specialist existingSpecialist = specialistRepository.findByUserId(specialistDto.getUserId())
                .orElseThrow(() -> new RuntimeException("Specialist not found for user ID: " + specialistDto.getUserId()));

        Integer currentVisits = existingSpecialist.getNumberOfVisits();
        existingSpecialist.setNumberOfVisits(currentVisits == null ? 1 : currentVisits + 1);

        existingSpecialist = specialistRepository.save(existingSpecialist);

        return mapToDto(existingSpecialist);
    }

    public Page<SpecialistDto> getSpecialistsAll(Pageable pageable) {
        return specialistRepository.findAll(pageable)
                .map(this::mapToDto);
    }

    public Page<SpecialistDto> getSpecialistsNutritionists(Pageable pageable) {
        return userRepository.findByRole("NUTRITIONIST", pageable)
                .map(user -> {
                    Specialist specialist = specialistRepository.findByUserId(user.getId())
                            .orElseThrow(() -> new RuntimeException("Specialist not found for user ID: " + user.getId()));
                    return mapToDto(specialist);
                });
    }

    public Page<SpecialistDto> getSpecialistsCoaches(Pageable pageable) {
        return userRepository.findByRole("COACH", pageable)
                .map(user -> {
                    Specialist specialist = specialistRepository.findByUserId(user.getId())
                            .orElseThrow(() -> new RuntimeException("Specialist not found for user ID: " + user.getId()));
                    return mapToDto(specialist);
                });
    }

    public Page<SpecialistDto> getSpecialistsByName(String name, Pageable pageable) {
        return specialistRepository.findByNameContainingIgnoreCase(name, pageable)
                .map(this::mapToDto);
    }
}
