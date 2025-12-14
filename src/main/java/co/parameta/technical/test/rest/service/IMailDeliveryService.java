package co.parameta.technical.test.rest.service;

import jakarta.mail.MessagingException;

import java.util.List;

public interface IMailDeliveryService {

     void sendText(
             String to,
             String subject,
             String html,
             byte[] fileBytes,
             String filename,
             List<String> cc,
             List<String> bcc
     ) throws MessagingException;
}
