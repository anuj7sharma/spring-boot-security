package com.learning.springSecurity.email;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Service
public class EmailService implements EmailSender {
    private static final Logger LOGGER = LoggerFactory
            .getLogger(EmailService.class);
    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine;

    @Value("${spring.mail.from}")
    private String fromEmail;

    @Override
    @Async
    public void send(Email email) throws MessagingException {
        MimeMessage mimeMessage = getMimeMessage(email);
        mailSender.send(mimeMessage);
    }

    private MimeMessage getMimeMessage(Email email) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper =
                new MimeMessageHelper(mimeMessage, true, StandardCharsets.UTF_8.name());
        String emailBody = templateEngine.process(email.getTemplateName(), getEmailContext(email.getValueModel()));
        helper.setText(emailBody, true);
        helper.setTo(email.getSendTo());
        helper.setSubject(email.getSubject());
        helper.setFrom(fromEmail);
        mailSender.send(mimeMessage);
        return mimeMessage;
    }

    private Context getEmailContext(Map<String, Object> valueMap) {
        Context context = new Context();
        context.setVariables(valueMap);
        return context;
    }
}
