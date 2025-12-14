package co.parameta.technical.test.rest.service;


import co.parameta.technical.test.rest.repository.EmployeeRepository;
import co.parameta.technical.test.rest.service.impl.S3PdfStorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class S3PdfStorageServiceTest {

    @InjectMocks
    private S3PdfStorageService service;

    @Mock
    private S3Client s3Client;

    @Mock
    private EmployeeRepository employeeRepository;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(service, "bucket", "test-bucket");
    }

    @Test
    void uploadPdfWithFilenameReturnsKeyAndSavesInDatabase() {
        byte[] pdfBytes = "PDF".getBytes();
        String filename = "PDF-ABCD.pdf";
        String documentNumber = "1111";
        String typeDocument = "CC";

        when(employeeRepository.searchIdEmployee(eq(documentNumber), eq(typeDocument)))
                .thenReturn(10);

        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenReturn(PutObjectResponse.builder().build());

        String key = service.uploadPdf(pdfBytes, filename, documentNumber, typeDocument);

        assertEquals("pdf/" + filename, key);

        ArgumentCaptor<PutObjectRequest> requestCaptor =
                ArgumentCaptor.forClass(PutObjectRequest.class);

        verify(s3Client, times(1))
                .putObject(requestCaptor.capture(), any(RequestBody.class));

        PutObjectRequest request = requestCaptor.getValue();
        assertEquals("test-bucket", request.bucket());
        assertEquals("pdf/" + filename, request.key());
        assertEquals("application/pdf", request.contentType());

        verify(employeeRepository, times(1))
                .searchIdEmployee(documentNumber, typeDocument);
        verify(employeeRepository, times(1))
                .saveReportEmployee("pdf/" + filename, 10);
    }

    @Test
    void uploadPdfWithNullFilenameUsesDefaultDocumentPdf() {
        byte[] pdfBytes = "PDF".getBytes();
        String documentNumber = "1111";
        String typeDocument = "CC";

        when(employeeRepository.searchIdEmployee(eq(documentNumber), eq(typeDocument)))
                .thenReturn(20);

        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenReturn(PutObjectResponse.builder().build());

        String key = service.uploadPdf(pdfBytes, null, documentNumber, typeDocument);

        assertEquals("pdf/document.pdf", key);

        verify(employeeRepository, times(1))
                .saveReportEmployee("pdf/document.pdf", 20);
    }

    @Test
    void uploadPdfWithBlankFilenameUsesDefaultDocumentPdf() {
        byte[] pdfBytes = "PDF".getBytes();
        String documentNumber = "1111";
        String typeDocument = "CC";

        when(employeeRepository.searchIdEmployee(eq(documentNumber), eq(typeDocument)))
                .thenReturn(30);

        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenReturn(PutObjectResponse.builder().build());

        String key = service.uploadPdf(pdfBytes, "   ", documentNumber, typeDocument);

        assertEquals("pdf/document.pdf", key);
        verify(employeeRepository, times(1))
                .saveReportEmployee("pdf/document.pdf", 30);
    }
}