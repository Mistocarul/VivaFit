package com.vivafit.vivafit.admin.controllers;

import com.vivafit.vivafit.admin.dto.ContactUsDto;
import com.vivafit.vivafit.admin.services.ContactUsService;
import com.vivafit.vivafit.authentification.responses.GeneralApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Contact Us", description = "Contact Us Controller")
@RequestMapping("/api/contact-us")
@RestController
@Validated
public class ContactUsController {

    @Autowired
    private ContactUsService contactUsService;

    @PostMapping("/send-message")
    public GeneralApiResponse sendMessage(@Valid @RequestBody ContactUsDto contactUsDto) {
        return contactUsService.sendMessage(contactUsDto);
    }
}
