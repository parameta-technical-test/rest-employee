package co.parameta.technical.test.rest.service.impl;

import co.parameta.technical.test.rest.service.IMailDeliveryService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MailDeliveryService implements IMailDeliveryService {

    private final JavaMailSender mailSender;

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
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

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
            helper.addAttachment(filename, new ByteArrayResource(fileBytes));
        }

        mailSender.send(message);
    }
}
