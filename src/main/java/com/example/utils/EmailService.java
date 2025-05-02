package com.example.utils;

import jakarta.mail.*;
import jakarta.mail.internet.*;

import java.util.Properties;

public class EmailService {

    private static final String FROM_EMAIL = System.getenv("EMAIL_FROM");
    private static final String APP_PASSWORD = System.getenv("EMAIL_PASSWORD");


    public static String sendConfirmationEmail(String toEmail, String username, String token, String baseUrl) {
        System.out.println("▶ Надсилання листа до: " + toEmail);
        System.out.println("▶ Токен: " + token);
        System.out.println("▶ Base URL: " + baseUrl);

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(FROM_EMAIL, APP_PASSWORD);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(FROM_EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("Підтвердіть свою електронну пошту");

            String confirmationLink = baseUrl + "/confirmemail?token=" + token;
            String htmlMessage = "<p>Привіт, <strong>" + username + "</strong>!</p>" +
                    "<p>Будь ласка, підтвердіть свою електронну пошту за посиланням:</p>" +
                    "<p><a href='" + confirmationLink + "'>Підтвердити Email</a></p>" +
                    "<p>Якщо ви не реєструвались, просто проігноруйте цей лист.</p>";

            message.setContent(htmlMessage, "text/html; charset=utf-8");

            Transport.send(message);
            System.out.println("✅ Лист успішно надіслано до: " + toEmail);
            return "✅ Лист з підтвердженням надіслано до " + toEmail;

        } catch (MessagingException e) {
            e.printStackTrace();
            return "❌ Не вдалося надіслати листа: " + e.getMessage();
        }
    }
}
