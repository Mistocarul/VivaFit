package com.vivafit.vivafit.specialist.controllers;

import com.vivafit.vivafit.specialist.dto.MessagesSpecialistsDto;
import com.vivafit.vivafit.specialist.services.MessagesSpecialistsService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
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

    @GetMapping("/get-messages-specialists")
    public List<MessagesSpecialistsDto> getMessagesSpecialists() {
        return messagesSpecialistsService.getMessagesSpecialists();
    }

    @PostMapping("/add-message-specialist")
    public MessagesSpecialistsDto addMessageSpecialist(@RequestBody MessagesSpecialistsDto messagesSpecialistsDto) {
        return messagesSpecialistsService.addMessageSpecialist(messagesSpecialistsDto);
    }

    @DeleteMapping("/delete-message-specialist/{id}")
    public void deleteMessageSpecialist(@PathVariable Integer id) {
        messagesSpecialistsService.deleteMessageSpecialist(id);
    }
}
