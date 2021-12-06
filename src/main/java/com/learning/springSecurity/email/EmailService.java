package com.learning.springSecurity.email;

import com.learning.springSecurity.entity.UserEntity;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Service
@AllArgsConstructor
public class EmailService implements EmailSender {
    private static final Logger LOGGER = LoggerFactory
            .getLogger(EmailService.class);
    private final JavaMailSender mailSender;

    @Override
    public Email buildRegisterConfirmationEmail(UserEntity userEntity, String confirmationLink) {
        Email email = new Email();
        email.setFrom("confirmation@anuj-acadamy.com");
        email.setTo(userEntity.getEmail());
        email.setSubject("Security Demo App Register Confirmation");
        email.setTemplateName("register_confirmation.html");
        Map<String, Object> valueMap = new HashMap<>();
        valueMap.put("firstName", userEntity.getFirstName());
        valueMap.put("lastName", userEntity.getLastName());
        valueMap.put("link", confirmationLink);
        email.setModel(valueMap);
        return email;
    }

    @Override
    @Async
    public void send(Email email) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper =
                    new MimeMessageHelper(mimeMessage, true, "utf-8");

            String emailBody = "<html><head></head><body><p>Hello User, Confirmation Link</p></body></html>";
            helper.setText(getEmailTemplate(email.getModel()), true);
            helper.setTo(email.getTo());
            helper.setSubject(email.getSubject());
            helper.setFrom(email.getFrom());
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            LOGGER.error("failed to send email", e);
            throw new IllegalStateException("failed to send email");
        }
    }

    private String getEmailTemplate(Map<String, Object> valueModel) {
        final Context ctx = new Context(Locale.US);
        ctx.setVariable("firstName", valueModel.get("firstName"));
        ctx.setVariable("lastName", valueModel.get("lastName"));
        ctx.setVariable("confirmationLink", valueModel.get("link"));
        final String htmlContent = emailTemplateEngine().process("register_confirmation.html", ctx);
        return htmlContent;
    }

    @Bean
    public org.thymeleaf.TemplateEngine emailTemplateEngine() {
        final org.thymeleaf.TemplateEngine templateEngine = new org.thymeleaf.TemplateEngine();
        return templateEngine;
    }
}
