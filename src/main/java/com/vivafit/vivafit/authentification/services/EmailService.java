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

    @Value("${sendgrid.api.key}")
    private String sendgridApiKey;
    @Value("${sendgrid.template.id}")
    private String sendgridTemplateId;

    public String sendEmail() {
        code = ThreadLocalRandom.current().nextInt(100000, 1000000);
        String fromEmail = "pauleusebiubejan2003@gmail.com";
        String toEmail = user.getEmail();

        Email from = new Email(fromEmail);
        Email to = new Email(toEmail);
        Mail mail = new Mail();
        mail.setFrom(from);

        Personalization personalization = new Personalization();
        personalization.addTo(to);

        mail.setTemplateId(sendgridTemplateId);

        personalization.addDynamicTemplateData("username", user.getUsername());
        personalization.addDynamicTemplateData("code", String.valueOf(code));
        mail.addPersonalization(personalization);

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
