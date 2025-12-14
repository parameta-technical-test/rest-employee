package co.parameta.technical.test.rest.service.impl;

import co.parameta.technical.test.rest.repository.EmployeeRepository;
import co.parameta.technical.test.rest.service.IS3PdfStorageService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

@Service
@RequiredArgsConstructor
public class S3PdfStorageService implements IS3PdfStorageService {

    private final S3Client s3;

    private final EmployeeRepository employeeRepository;

    @Value("${aws.s3.bucket}")
    private String bucket;

    @Override
    @Transactional
    public String uploadPdf(byte[] pdfBytes, String originalFilename, String documentNumber, String typeDocument) {
        String safeName = (originalFilename == null || originalFilename.isBlank())
                ? "document.pdf"
                : originalFilename;

        String key = "pdf/" + safeName;

        PutObjectRequest req = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType("application/pdf")
                .build();

        s3.putObject(req, RequestBody.fromBytes(pdfBytes));
        employeeRepository.saveReportEmployee(key, employeeRepository.searchIdEmployee(documentNumber, typeDocument));
        return key;
    }
}
