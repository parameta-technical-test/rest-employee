package co.parameta.technical.test.rest.service;

import co.parameta.technical.test.commons.dto.SystemParameterDTO;
import co.parameta.technical.test.commons.util.mapper.SystemParameterMapper;
import co.parameta.technical.test.rest.dto.EmployeeRequestDTO;
import co.parameta.technical.test.rest.repository.SystemParameterRepository;
import co.parameta.technical.test.rest.service.impl.PrepareMailDeliveryService;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PrepareMailDeliveryServiceTest {

    @InjectMocks
    private PrepareMailDeliveryService service;

    @Mock
    private SystemParameterMapper systemParameterMapper;

    @Mock
    private IEmployeePdfGeneratorService employeePdfGeneratorService;

    @Mock
    private IMailDeliveryService mailDeliveryService;

    @Mock
    private SystemParameterRepository systemParameterRepository;

    @Mock
    private IS3PdfStorageService s3PdfStorageService;

    @Test
    void prepareMailDeliveryWithAttachmentUploadsToS3AndSendsWithCcBcc()
            throws MessagingException {

        EmployeeRequestDTO employee = new EmployeeRequestDTO();
        employee.setNames("Brahian");
        employee.setLastNames("Caceres");
        employee.setTypeDocument("CC");
        employee.setDocumentNumber("1111");
        employee.setEmail("to@test.com");

        List<SystemParameterDTO> params = List.of(
                param("EMAIL_SUBJECT", "Subject"),
                param("EMAIL_CONTENT", "<b>HTML</b>"),
                param("EMAIL_COPY", "cc1@test.com, cc2@test.com"),
                param("EMAIL_SEND_ATTACHMENT", "1"),
                param("SEND_EMAIL_WITH_COPY", "1"),
                param("SEND_EMAIL_WITH_BLIND_COPY", "1"),
                param("BLIND_COPY_EMAILS", "bcc@test.com")
        );

        when(systemParameterRepository.searchAllParameters(anyList()))
                .thenReturn(Collections.emptyList());

        when(systemParameterMapper.toListDto(any()))
                .thenReturn(params);

        byte[] pdfBytes = "PDF".getBytes();
        when(employeePdfGeneratorService.generateEmployeeReport(any(EmployeeRequestDTO.class)))
                .thenReturn(pdfBytes);

        service.prepareMailDelivery(employee);

        verify(employeePdfGeneratorService, times(1))
                .generateEmployeeReport(employee);

        ArgumentCaptor<String> fileNameCaptor =
                ArgumentCaptor.forClass(String.class);

        verify(s3PdfStorageService, times(1))
                .uploadPdf(eq(pdfBytes), fileNameCaptor.capture(), eq("1111"), eq("CC"));

        String generatedFileName = fileNameCaptor.getValue();
        assertNotNull(generatedFileName);
        assertTrue(generatedFileName.startsWith("PDF-"));
        assertTrue(generatedFileName.endsWith(".pdf"));

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<String>> ccCaptor = ArgumentCaptor.forClass(List.class);
        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<String>> bccCaptor = ArgumentCaptor.forClass(List.class);

        verify(mailDeliveryService, times(1)).sendText(
                eq("to@test.com"),
                eq("Subject"),
                eq("<b>HTML</b>"),
                eq(pdfBytes),
                eq(generatedFileName),
                ccCaptor.capture(),
                bccCaptor.capture()
        );

        assertEquals(List.of("cc1@test.com", "cc2@test.com"), ccCaptor.getValue());
        assertEquals(List.of("bcc@test.com"), bccCaptor.getValue());
    }

    @Test
    void prepareMailDeliveryWithoutAttachmentDoesNotUploadToS3AndSendsWithoutAttachment()
            throws MessagingException {

        EmployeeRequestDTO employee = new EmployeeRequestDTO();
        employee.setNames("Brahian");
        employee.setLastNames("Caceres");
        employee.setTypeDocument("CC");
        employee.setDocumentNumber("1111");
        employee.setEmail("to@test.com");

        List<SystemParameterDTO> params = List.of(
                param("EMAIL_SUBJECT", "Subject"),
                param("EMAIL_CONTENT", "<b>HTML</b>"),
                param("EMAIL_COPY", "cc1@test.com, cc2@test.com"),
                param("EMAIL_SEND_ATTACHMENT", "0"),
                param("SEND_EMAIL_WITH_COPY", "1"),
                param("SEND_EMAIL_WITH_BLIND_COPY", "0"),
                param("BLIND_COPY_EMAILS", "bcc@test.com")
        );

        when(systemParameterRepository.searchAllParameters(anyList()))
                .thenReturn(Collections.emptyList());

        when(systemParameterMapper.toListDto(any()))
                .thenReturn(params);

        service.prepareMailDelivery(employee);

        verify(employeePdfGeneratorService, never()).generateEmployeeReport(any());
        verify(s3PdfStorageService, never())
                .uploadPdf(any(), anyString(), anyString(), anyString());

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<String>> ccCaptor = ArgumentCaptor.forClass(List.class);
        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<String>> bccCaptor = ArgumentCaptor.forClass(List.class);

        verify(mailDeliveryService, times(1)).sendText(
                eq("to@test.com"),
                eq("Subject"),
                eq("<b>HTML</b>"),
                isNull(),
                isNull(),
                ccCaptor.capture(),
                bccCaptor.capture()
        );

        assertEquals(List.of("cc1@test.com", "cc2@test.com"), ccCaptor.getValue());
        assertEquals(List.of(), bccCaptor.getValue());
    }

    private static SystemParameterDTO param(String name, String content) {
        SystemParameterDTO dto = new SystemParameterDTO();
        dto.setName(name);
        dto.setContent(content);
        return dto;
    }
}