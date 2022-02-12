package com.agilemonkeys.crmapi.service;

import com.sendgrid.Content;
import com.sendgrid.Email;
import com.sendgrid.Mail;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Slf4j
public class NotificationService {

    @Value("${crmapi.from-email}")
    private String fromEmail;

    @Value("${crmapi.admin-email}")
    private String adminEmail;

    public NotificationService() {}

    public void sendEmail(String subject, String message) throws IOException {
        Email from = new Email(fromEmail);
        Email to = new Email(adminEmail);
        Content content = new Content(MediaType.TEXT_PLAIN_VALUE, message);
        Mail mail = new Mail(from, subject, to, content);

        SendGrid sg = new SendGrid(System.getenv("SENDGRID_API_KEY"));
        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);
            log.trace("Response received from SendGrid: Status {} {} {}", response.getStatusCode(), response.getBody(), response.getBody());
            log.debug("Notification sent successfully to admin email.");
        } catch (IOException ex) {
            throw ex;
        }
    }

}
