package co.parameta.technical.test.rest.service;

import co.parameta.technical.test.commons.dto.ScriptValidationDTO;
import co.parameta.technical.test.commons.dto.SystemParameterDTO;
import co.parameta.technical.test.commons.entity.ScriptValidationEntity;
import co.parameta.technical.test.commons.entity.SystemParameterEntity;
import co.parameta.technical.test.commons.util.mapper.ScriptValidationMapper;
import co.parameta.technical.test.commons.util.mapper.SystemParameterMapper;
import co.parameta.technical.test.rest.dto.EmployeeRequestDTO;
import co.parameta.technical.test.rest.repository.ScriptValidationRepository;
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
    private IEmployeePdfGeneratorService iEmployeePdfGeneratorService;

    @Mock
    private IMailDeliveryService iMailDeliveryService;

    @Mock
    private SystemParameterRepository systemParameterRepository;

    @Mock
    private IS3PdfStorageService is3PdfStorageService;

    @Mock
    private IGroovieScriptExecutorService iGroovieScriptExecutorService;

    @Mock
    private ScriptValidationRepository scriptValidationRepository;

    @Mock
    private ScriptValidationMapper scriptValidationMapper;

    @Test
    void prepareMailDeliveryCreateWithAttachmentUploadsToS3AndSendsWithCcBccAndGroovyContent()
            throws MessagingException {

        EmployeeRequestDTO employee = baseEmployee();

        SystemParameterDTO updateInfo = param("UPDATE_INFORMATION", "0");
        when(systemParameterRepository.findByName("UPDATE_INFORMATION")).thenReturn(mock(SystemParameterEntity.class));
        when(systemParameterMapper.toDto(any())).thenReturn(updateInfo);

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

        when(scriptValidationRepository.findByCode("CAST_CONTENT_EMAIL")).thenReturn(mock(ScriptValidationEntity.class));
        when(scriptValidationMapper.toDto(any())).thenReturn(mock(ScriptValidationDTO.class));

        when(iGroovieScriptExecutorService.runScript(any(), anyMap(), anyList()))
                .thenReturn("CONTENT_FROM_GROOVY");

        byte[] pdfBytes = "PDF".getBytes();
        when(iEmployeePdfGeneratorService.generateEmployeeReport(any(EmployeeRequestDTO.class), eq(false)))
                .thenReturn(pdfBytes);

        service.prepareMailDelivery(employee, false);

        verify(iEmployeePdfGeneratorService, times(1))
                .generateEmployeeReport(employee, false);

        ArgumentCaptor<String> fileNameCaptor = ArgumentCaptor.forClass(String.class);
        verify(is3PdfStorageService, times(1))
                .uploadPdf(eq(pdfBytes), fileNameCaptor.capture(), eq("1111"), eq("CC"));

        String generatedFileName = fileNameCaptor.getValue();
        assertNotNull(generatedFileName);
        assertTrue(generatedFileName.startsWith("PDF-"));
        assertTrue(generatedFileName.endsWith(".pdf"));

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<String>> ccCaptor = ArgumentCaptor.forClass(List.class);
        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<String>> bccCaptor = ArgumentCaptor.forClass(List.class);

        verify(iMailDeliveryService, times(1)).sendText(
                eq("to@test.com"),
                eq("Subject"),
                eq("CONTENT_FROM_GROOVY"),
                eq(pdfBytes),
                eq(generatedFileName),
                ccCaptor.capture(),
                bccCaptor.capture()
        );

        assertEquals(List.of("cc1@test.com", "cc2@test.com"), ccCaptor.getValue());
        assertEquals(List.of("bcc@test.com"), bccCaptor.getValue());

        verify(scriptValidationRepository, times(1)).findByCode("CAST_CONTENT_EMAIL");
        verify(systemParameterRepository, times(1)).findByName("UPDATE_INFORMATION");
    }

    @Test
    void prepareMailDeliveryCreateWithoutAttachmentDoesNotUploadToS3AndSendsWithoutAttachment()
            throws MessagingException {

        EmployeeRequestDTO employee = baseEmployee();

        SystemParameterDTO updateInfo = param("UPDATE_INFORMATION", "0");
        when(systemParameterRepository.findByName("UPDATE_INFORMATION")).thenReturn(mock(SystemParameterEntity.class));
        when(systemParameterMapper.toDto(any())).thenReturn(updateInfo);

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

        when(scriptValidationRepository.findByCode("CAST_CONTENT_EMAIL")).thenReturn(mock(ScriptValidationEntity.class));
        when(scriptValidationMapper.toDto(any())).thenReturn(mock(ScriptValidationDTO.class));
        when(iGroovieScriptExecutorService.runScript(any(), anyMap(), anyList()))
                .thenReturn("CONTENT_FROM_GROOVY");

        service.prepareMailDelivery(employee, false);

        verify(iEmployeePdfGeneratorService, never()).generateEmployeeReport(any(), anyBoolean());
        verify(is3PdfStorageService, never()).uploadPdf(any(), anyString(), anyString(), anyString());

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<String>> ccCaptor = ArgumentCaptor.forClass(List.class);
        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<String>> bccCaptor = ArgumentCaptor.forClass(List.class);

        verify(iMailDeliveryService, times(1)).sendText(
                eq("to@test.com"),
                eq("Subject"),
                eq("CONTENT_FROM_GROOVY"),
                isNull(),
                isNull(),
                ccCaptor.capture(),
                bccCaptor.capture()
        );

        assertEquals(List.of("cc1@test.com", "cc2@test.com"), ccCaptor.getValue());
        assertEquals(List.of(), bccCaptor.getValue());
    }

    @Test
    void prepareMailDeliveryUpdateWithParamUsesUpdateParametersAndUpdateScriptAndUploadsAndSends()
            throws MessagingException {

        EmployeeRequestDTO employee = baseEmployee();

        SystemParameterDTO updateInfo = param("UPDATE_INFORMATION", "1");
        when(systemParameterRepository.findByName("UPDATE_INFORMATION")).thenReturn(mock(SystemParameterEntity.class));
        when(systemParameterMapper.toDto(any())).thenReturn(updateInfo);

        List<SystemParameterDTO> paramsUpdate = List.of(
                param("EMAIL_SUBJECT_UPDATE", "Subject U"),
                param("EMAIL_CONTENT_UPDATE", "<b>HTML U</b>"),
                param("EMAIL_COPY_UPDATE", "ccU1@test.com, ccU2@test.com"),
                param("EMAIL_SEND_ATTACHMENT_UPDATE", "1"),
                param("SEND_EMAIL_WITH_COPY_UPDATE", "1"),
                param("SEND_EMAIL_WITH_BLIND_COPY_UPDATE", "1"),
                param("BLIND_COPY_EMAILS_UPDATE", "bccU@test.com")
        );

        when(systemParameterRepository.searchAllParameters(anyList()))
                .thenReturn(Collections.emptyList());
        when(systemParameterMapper.toListDto(any()))
                .thenReturn(paramsUpdate);

        when(scriptValidationRepository.findByCode("CAST_CONTENT_EMAIL_UPDATE")).thenReturn(mock(ScriptValidationEntity.class));
        when(scriptValidationMapper.toDto(any())).thenReturn(mock(ScriptValidationDTO.class));

        when(iGroovieScriptExecutorService.runScript(any(), anyMap(), anyList()))
                .thenReturn("CONTENT_UPDATE_FROM_GROOVY");

        byte[] pdfBytes = "PDFU".getBytes();
        when(iEmployeePdfGeneratorService.generateEmployeeReport(any(EmployeeRequestDTO.class), eq(true)))
                .thenReturn(pdfBytes);

        service.prepareMailDelivery(employee, true);

        verify(iEmployeePdfGeneratorService, times(1))
                .generateEmployeeReport(employee, true);

        ArgumentCaptor<String> fileNameCaptor = ArgumentCaptor.forClass(String.class);
        verify(is3PdfStorageService, times(1))
                .uploadPdf(eq(pdfBytes), fileNameCaptor.capture(), eq("1111"), eq("CC"));

        String generatedFileName = fileNameCaptor.getValue();
        assertNotNull(generatedFileName);
        assertTrue(generatedFileName.startsWith("PDF-"));
        assertTrue(generatedFileName.endsWith(".pdf"));

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<String>> ccCaptor = ArgumentCaptor.forClass(List.class);
        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<String>> bccCaptor = ArgumentCaptor.forClass(List.class);

        verify(iMailDeliveryService, times(1)).sendText(
                eq("to@test.com"),
                eq("Subject U"),
                eq("CONTENT_UPDATE_FROM_GROOVY"),
                eq(pdfBytes),
                eq(generatedFileName),
                ccCaptor.capture(),
                bccCaptor.capture()
        );

        assertEquals(List.of("ccU1@test.com", "ccU2@test.com"), ccCaptor.getValue());
        assertEquals(List.of("bccU@test.com"), bccCaptor.getValue());

        verify(scriptValidationRepository, times(1)).findByCode("CAST_CONTENT_EMAIL_UPDATE");
        verify(scriptValidationRepository, never()).findByCode("CAST_CONTENT_EMAIL");
    }

    private static EmployeeRequestDTO baseEmployee() {
        EmployeeRequestDTO employee = new EmployeeRequestDTO();
        employee.setNames("Brahian");
        employee.setLastNames("Caceres");
        employee.setTypeDocument("CC");
        employee.setDocumentNumber("1111");
        employee.setEmail("to@test.com");
        return employee;
    }

    private static SystemParameterDTO param(String name, String content) {
        SystemParameterDTO dto = new SystemParameterDTO();
        dto.setName(name);
        dto.setContent(content);
        return dto;
    }
}
