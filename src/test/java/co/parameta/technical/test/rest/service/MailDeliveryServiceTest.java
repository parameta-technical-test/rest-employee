package co.parameta.technical.test.rest.service;

import co.parameta.technical.test.rest.service.impl.MailDeliveryService;
import jakarta.mail.Address;
import jakarta.mail.Message;
import jakarta.mail.Session;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.List;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MailDeliveryServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private MailDeliveryService mailDeliveryService;

    @Test
    void sendTextWithAttachmentCcBccSendsEmail() throws Exception {
        MimeMessage mimeMessage = new MimeMessage(Session.getInstance(new Properties()));
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        String to = "to@test.com";
        String subject = "Subject";
        String html = "<b>Hello</b>";
        byte[] fileBytes = "PDF".getBytes();
        String filename = "file.pdf";
        List<String> cc = List.of("cc1@test.com", "cc2@test.com");
        List<String> bcc = List.of("bcc@test.com");

        mailDeliveryService.sendText(
                to, subject, html, fileBytes, filename, cc, bcc
        );

        verify(mailSender, times(1)).send(mimeMessage);

        Address[] toAddr = mimeMessage.getRecipients(Message.RecipientType.TO);
        assertNotNull(toAddr);
        assertEquals(1, toAddr.length);
        assertEquals(to, ((InternetAddress) toAddr[0]).getAddress());

        assertEquals(subject, mimeMessage.getSubject());

        Address[] ccAddr = mimeMessage.getRecipients(Message.RecipientType.CC);
        assertNotNull(ccAddr);
        assertEquals(2, ccAddr.length);

        Address[] bccAddr = mimeMessage.getRecipients(Message.RecipientType.BCC);
        assertNotNull(bccAddr);
        assertEquals(1, bccAddr.length);
    }

    @Test
    void sendTextWithoutAttachmentAndCcBccSendsEmail() throws Exception {
        MimeMessage mimeMessage = new MimeMessage(Session.getInstance(new Properties()));
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        mailDeliveryService.sendText(
                "to@test.com",
                "Subject",
                "<p>Hi</p>",
                null,
                null,
                null,
                null
        );

        verify(mailSender, times(1)).send(mimeMessage);

        assertNull(mimeMessage.getRecipients(Message.RecipientType.CC));
        assertNull(mimeMessage.getRecipients(Message.RecipientType.BCC));
    }

    @Test
    void sendTextWithNullFilenameDoesNotAddAttachmentAndSendsEmail() throws Exception {
        MimeMessage mimeMessage = new MimeMessage(Session.getInstance(new Properties()));
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        mailDeliveryService.sendText(
                "to@test.com",
                "Subject",
                "<p>Hi</p>",
                "PDF".getBytes(),
                null,
                null,
                null
        );

        verify(mailSender, times(1)).send(mimeMessage);
    }
}