package org.springboot.smartcontactmanager.service;

import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.PasswordAuthentication;
import java.util.Properties;

@Service
public class EmailService {

    public boolean sendEmail(String subject, String message, String to) {

        String from = "9417446746yishu@gmail.com";

        boolean status = false;

        //Variable for gmail.
        String host = "smtp.gmail.com"; //Host for "Gmail" server. "SMTP" -> "Simple Mail Transfer Protocol".

        //Get the system properties.
        Properties properties = System.getProperties();
        System.out.println("Properties: " + properties);

        //Setting the important information to properties object.
        properties.put("mail.smtp.host", host); //for setting "Host".
        properties.put("mail.smtp.port", "465");
        properties.put("mail.smtp.ssl.enable", "true"); //"SSL" -> "Secure Socket Layer", for security purpose.
        properties.put("mail.smtp.auth", "true"); //for enabling "Authentication".

        //Step 1. Get the "Session" object.
        Session session = Session.getInstance(properties, new Authenticator() {

            @Override
            protected PasswordAuthentication getPasswordAuthentication() {

                return new PasswordAuthentication("9417446746yishu@gmail.com", "YishuKumar-780-05-O-00-@"); //From which we want send email.
            }
        });

        session.setDebug(true); //To get debug message for error.

        //Step 2. Compose the message (text, multi media).
        MimeMessage mimeMessage = new MimeMessage(session);

        try {

            mimeMessage.setFrom(from); //Set "From".
            mimeMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(to)); //Adding "Recipient" to message. We can pass th array of "Addresses" like "InternetAddress()".
            mimeMessage.setSubject(subject); //Set the message subject.
//            mimeMessage.setText(message); //Set the message.
            mimeMessage.setContent(message, "text/html"); //Set the message and its content type to allow its type like "HTML" etc.

            //Step 3. Send the message using "Transport" class.
            Transport.send(mimeMessage);

            status = true;

            System.out.println("Sent message...........................");

        } catch (MessagingException e) {

            System.out.println(e.getMessage());
            e.printStackTrace();
        }

        return status;
    }

}
