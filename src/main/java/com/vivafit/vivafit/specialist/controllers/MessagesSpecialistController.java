package com.vivafit.vivafit.specialist.controllers;

import com.vivafit.vivafit.authentification.entities.User;
import com.vivafit.vivafit.authentification.services.JwtService;
import com.vivafit.vivafit.specialist.dto.MessagesSpecialistsDto;
import com.vivafit.vivafit.specialist.services.MessagesSpecialistsService;
import com.vivafit.vivafit.specialist.services.SpecialistService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Messages Specialist", description = "Messages Specialists Controller")
@RequestMapping("/api/messages-specialists")
@RestController
@Validated
public class MessagesSpecialistController {
    @Autowired
    private MessagesSpecialistsService messagesSpecialistsService;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private SpecialistService specialistService;

    @GetMapping("/get-messages-specialists")
    public List<MessagesSpecialistsDto> getMessagesSpecialists(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        User currentUser = jwtService.validateAndGetCurrentUser(authorizationHeader);
        Integer specialistId = specialistService.getSpecialistIdByUserId(currentUser.getId());
        return messagesSpecialistsService.getMessagesSpecialists(specialistId);
    }

    @PostMapping("/add-message-specialist")
    public MessagesSpecialistsDto addMessageSpecialist(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader, @RequestBody MessagesSpecialistsDto messagesSpecialistsDto) {
        User currentUser = jwtService.validateAndGetCurrentUser(authorizationHeader);
        messagesSpecialistsDto.setUserId(currentUser.getId());
        return messagesSpecialistsService.addMessageSpecialist(messagesSpecialistsDto);
    }

    @DeleteMapping("/delete-message-specialist/{id}")
    public void deleteMessageSpecialist(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader, @PathVariable Integer id) {
        User currentUser = jwtService.validateAndGetCurrentUser(authorizationHeader);
        messagesSpecialistsService.deleteMessageSpecialist(id);
    }
}
