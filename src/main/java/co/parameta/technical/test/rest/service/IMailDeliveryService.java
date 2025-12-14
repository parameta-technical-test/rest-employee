package co.parameta.technical.test.rest.service;

import jakarta.mail.MessagingException;

import java.util.List;

/**
 * Service interface responsible for sending emails.
 * <p>
 * Supports HTML content, optional file attachments, and carbon copy (CC)
 * and blind carbon copy (BCC) recipients.
 * </p>
 */
public interface IMailDeliveryService {

     /**
      * Sends an email message with optional attachment, CC, and BCC recipients.
      *
      * @param to        the primary recipient email address
      * @param subject   the email subject
      * @param html      the email body in HTML format
      * @param fileBytes the attachment file content as bytes (optional)
      * @param filename  the attachment file name (optional)
      * @param cc        list of CC (carbon copy) recipient email addresses (optional)
      * @param bcc       list of BCC (blind carbon copy) recipient email addresses (optional)
      * @throws MessagingException if an error occurs while creating or sending the email
      */
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
