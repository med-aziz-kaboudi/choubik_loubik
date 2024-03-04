package utils;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class Mailing {

    public static void sendEmail(String recipientEmail, String subject, String messageBody) {
        Properties props = new Properties();
        props.put("mail.smtp.host",  "smtp-mail.outlook.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        String username = "my outlook"; // Use environment variables or config
        String password = "password"; // Use environment variables or config

        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject(subject);
            message.setText(messageBody);

            Transport.send(message);

            System.out.println("Email sent successfully to: " + recipientEmail);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
