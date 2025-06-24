package com.vivafit.vivafit.specialist.services;

import com.vivafit.vivafit.specialist.dto.MessagesSpecialistsDto;
import com.vivafit.vivafit.specialist.entities.MessagesSpecialists;
import com.vivafit.vivafit.specialist.repositories.MessagesSpecialistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MessagesSpecialistsService {
    @Autowired
    private MessagesSpecialistRepository messagesSpecialistRepository;

    public List<MessagesSpecialistsDto> getMessagesSpecialists(Integer specialistId) {
        return Optional.ofNullable(messagesSpecialistRepository.findAllBySpecialistId(specialistId))
                .orElse(List.of())
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public MessagesSpecialistsDto addMessageSpecialist(MessagesSpecialistsDto messagesSpecialistsDto) {
        MessagesSpecialists message = mapToEntity(messagesSpecialistsDto);
        MessagesSpecialists savedMessage = messagesSpecialistRepository.save(message);
        return mapToDto(savedMessage);
    }

    public void deleteMessageSpecialist(Integer id) {
        messagesSpecialistRepository.deleteById(id);
    }

    private MessagesSpecialistsDto mapToDto(MessagesSpecialists entity) {
        return new MessagesSpecialistsDto(
                entity.getId(),
                entity.getUserId(),
                entity.getSpecialistId(),
                entity.getUserCompleteName(),
                entity.getUserPhoneNumber(),
                entity.getMessage(),
                entity.getUserEmail(),
                entity.getCreatedAt().toString()
        );
    }

    private MessagesSpecialists mapToEntity(MessagesSpecialistsDto dto) {
        return MessagesSpecialists.builder()
                .userId(dto.getUserId())
                .specialistId(dto.getSpecialistId())
                .userCompleteName(dto.getUserCompletName())
                .userPhoneNumber(dto.getUserPhoneNumber())
                .message(dto.getMessage())
                .userEmail(dto.getUserEmail())
                .createdAt(String.valueOf(dto.getCreatedAt() != null ? java.time.LocalDateTime.parse(dto.getCreatedAt()) : java.time.LocalDateTime.now()))
                .build();
    }
}
