package com.vivafit.vivafit.authentification.services;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;
import com.vivafit.vivafit.authentification.entities.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadLocalRandom;

@Getter
@Setter
@Service
public class EmailService {
    private User user;
    private int code;
    private int whatSituation;
    private String resetLink;
    private String ipAdress;
    private String userAgent;
    private String device;

    @Value("${sendgrid.api.key}")
    private String sendgridApiKey;
    @Value("${sendgrid.template.id1}")
    private String sendgridTemplateId1;
    @Value("${sendgrid.template.id2}")
    private String sendgridTemplateId2;
    @Value("${sendgrid.template.id3}")
    private String sendgridTemplateId3;
    @Value("${sendgrid.from.email}")
    private String fromEmail;

    public String sendEmail() {
        String toEmail = user.getEmail();
        String fromName = "VivaFit";
        Email from = new Email(fromEmail, fromName);
        Email to = new Email(toEmail);
        Mail mail = new Mail();
        mail.setFrom(from);

        if(whatSituation == 0){
            code = ThreadLocalRandom.current().nextInt(100000, 1000000);
            Personalization personalization = new Personalization();
            personalization.addTo(to);

            mail.setTemplateId(sendgridTemplateId1);

            personalization.addDynamicTemplateData("username", user.getUsername());
            personalization.addDynamicTemplateData("code", String.valueOf(code));
            mail.addPersonalization(personalization);
        }
        else if(whatSituation == 1){
            Personalization personalization = new Personalization();
            personalization.addTo(to);

            mail.setTemplateId(sendgridTemplateId2);

            personalization.addDynamicTemplateData("username", user.getUsername());
            personalization.addDynamicTemplateData("resetLink", resetLink);
            mail.addPersonalization(personalization);
        }
        else if (whatSituation == 2){
            Personalization personalization = new Personalization();
            personalization.addTo(to);

            mail.setTemplateId(sendgridTemplateId3);

            personalization.addDynamicTemplateData("username", user.getUsername());
            personalization.addDynamicTemplateData("ipAdress", ipAdress);
            personalization.addDynamicTemplateData("userAgent", userAgent);
            personalization.addDynamicTemplateData("device", device);
            mail.addPersonalization(personalization);
        }


        SendGrid sendGrid = new SendGrid(sendgridApiKey);
        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sendGrid.api(request);
            if (response.getStatusCode() != 202) {
                throw new RuntimeException("Failed to send email: " + response.getBody());
            }
            return response.getBody();
        } catch (Exception exception) {
            throw new RuntimeException("Failed to send email: " + exception.getMessage());
        }

    }
}
