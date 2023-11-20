package br.com.orchestrated.pattern.orderregisterservice.service;

import br.com.orchestrated.pattern.orderregisterservice.dto.EmailDto;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value(value = "${spring.mail.username}")
    private String emailFrom;

    public void sendEmail(EmailDto emailDto) {

        emailDto.setFrom(emailFrom);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(emailDto.getTo());
        message.setSubject(emailDto.getSubject());
        message.setText(emailDto.getText());
        mailSender.send(message);
        log.info("Email sent successfully to {}", emailDto.getTo());
    }

}
