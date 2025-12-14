package co.parameta.technical.test.rest.service.impl;

import co.parameta.technical.test.rest.repository.EmployeeRepository;
import co.parameta.technical.test.rest.service.IS3PdfStorageService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

/**
 * Service responsible for storing employee PDF reports in Amazon S3.
 * <p>
 * After a successful upload, the generated S3 key is persisted
 * in the employee record for later retrieval.
 */
@Service
@RequiredArgsConstructor
public class S3PdfStorageService implements IS3PdfStorageService {

    private final S3Client s3;
    private final EmployeeRepository employeeRepository;

    @Value("${aws.s3.bucket}")
    private String bucket;

    /**
     * Uploads an employee PDF report to S3 and associates it with the employee.
     * <p>
     * If the original filename is null or blank, a default name is used.
     * The operation is transactional to ensure consistency between
     * S3 storage and database update.
     *
     * @param pdfBytes        PDF content to upload
     * @param originalFilename original file name (optional)
     * @param documentNumber  employee document number
     * @param typeDocument    employee document type
     * @return the generated S3 object key
     */
    @Override
    @Transactional
    public String uploadPdf(
            byte[] pdfBytes,
            String originalFilename,
            String documentNumber,
            String typeDocument
    ) {

        String safeName = (originalFilename == null || originalFilename.isBlank())
                ? "document.pdf"
                : originalFilename;

        String key = "pdf/" + safeName;

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType("application/pdf")
                .build();

        s3.putObject(request, RequestBody.fromBytes(pdfBytes));

        employeeRepository.saveReportEmployee(
                key,
                employeeRepository.searchIdEmployee(documentNumber, typeDocument)
        );

        return key;
    }
}
