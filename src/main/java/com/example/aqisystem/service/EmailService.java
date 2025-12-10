package com.example.aqisystem.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendEmail(String to, String subject, String body) {
        if (to == null || to.trim().isEmpty()) {
            throw new IllegalArgumentException("Email recipient cannot be null or empty");
        }

        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setTo(to);
            msg.setSubject(subject);
            msg.setText(body);
            mailSender.send(msg);
            System.out.println("Email successfully sent to: " + to);
        } catch (Exception e) {
            System.err.println("Failed to send email to " + to + ": " + e.getMessage());
            throw new RuntimeException("Email sending failed: " + e.getMessage(), e);
        }
    }
}
