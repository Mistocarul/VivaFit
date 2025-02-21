package com.vivafit.vivafit.admin.services;

import com.vivafit.vivafit.admin.dto.ContactUsDto;
import com.vivafit.vivafit.admin.entities.ContactUs;
import com.vivafit.vivafit.admin.repositories.ContactUsRepository;
import com.vivafit.vivafit.authentification.responses.GeneralApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Calendar;

@Service
public class ContactUsService {

    @Autowired
    private ContactUsRepository contactUsRepository;

    public GeneralApiResponse sendMessage(ContactUsDto contactUsDto) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date startOfDay = calendar.getTime();

        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        Date endOfDay = calendar.getTime();

        List<ContactUs> messages = contactUsRepository.findByEmailOrPhoneNumberAndCreatedAtBetween(
                contactUsDto.getEmail(), contactUsDto.getPhoneNumber(), startOfDay, endOfDay);

        if (!messages.isEmpty()) {
            return new GeneralApiResponse("You have already sent a message today");
        }

        ContactUs contactUs = new ContactUs();
        contactUs.setFirstName(contactUsDto.getFirstName());
        contactUs.setLastName(contactUsDto.getLastName());
        contactUs.setEmail(contactUsDto.getEmail());
        contactUs.setPhoneNumber(contactUsDto.getPhoneNumber());
        contactUs.setMessage(contactUsDto.getMessage());
        contactUs.setCreatedAt(new Date());
        contactUsRepository.save(contactUs);
        return new GeneralApiResponse("Message sent successfully");
    }
}