package co.parameta.technical.test.rest.service.impl;

import co.parameta.technical.test.rest.service.IMailDeliveryService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service implementation responsible for sending emails with optional
 * HTML content, attachments, CC and BCC recipients.
 *
 * <p>
 * This service uses Spring's {@link JavaMailSender} to build and send
 * MIME-compliant emails, allowing:
 * </p>
 * <ul>
 *     <li>HTML email bodies</li>
 *     <li>File attachments (e.g. generated PDFs)</li>
 *     <li>Optional CC and BCC recipients</li>
 *     <li>UTF-8 encoding support</li>
 * </ul>
 *
 * <p>
 * It is commonly used to notify employees or administrators after
 * validation or registration processes.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class MailDeliveryService implements IMailDeliveryService {

    /**
     * Spring mail sender used to dispatch email messages.
     */
    private final JavaMailSender mailSender;

    /**
     * Sends an email with HTML content and optional attachment.
     *
     * <p>
     * The email supports:
     * <ul>
     *     <li>HTML body content</li>
     *     <li>Optional file attachment</li>
     *     <li>Optional CC recipients</li>
     *     <li>Optional BCC recipients</li>
     * </ul>
     * </p>
     *
     * @param to        primary recipient email address
     * @param subject   email subject
     * @param html      email body in HTML format
     * @param fileBytes file content to attach (optional)
     * @param filename  attachment filename (required if {@code fileBytes} is provided)
     * @param cc        list of CC email addresses (optional)
     * @param bcc       list of BCC email addresses (optional)
     *
     * @throws MessagingException if an error occurs while creating or sending the email
     */
    @Override
    public void sendText(
            String to,
            String subject,
            String html,
            byte[] fileBytes,
            String filename,
            List<String> cc,
            List<String> bcc
    ) throws MessagingException {

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper =
                new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(html, true);

        if (cc != null && !cc.isEmpty()) {
            helper.setCc(cc.toArray(new String[0]));
        }

        if (bcc != null && !bcc.isEmpty()) {
            helper.setBcc(bcc.toArray(new String[0]));
        }

        if (fileBytes != null && fileBytes.length > 0 && filename != null) {
            helper.addAttachment(
                    filename,
                    new ByteArrayResource(fileBytes)
            );
        }

        mailSender.send(message);
    }
}
