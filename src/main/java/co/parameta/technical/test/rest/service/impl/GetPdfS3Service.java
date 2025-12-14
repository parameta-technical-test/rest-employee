package co.parameta.technical.test.rest.service.impl;

import co.parameta.technical.test.rest.service.IGetPdfS3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

import java.io.IOException;

/**
 * Service implementation responsible for retrieving PDF files from Amazon S3.
 * <p>
 * This service downloads a PDF document from an S3 bucket using the provided
 * object key and returns its content as a byte array.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class GetPdfS3Service implements IGetPdfS3Service {

    /**
     * Amazon S3 client used to interact with the S3 service.
     */
    private final S3Client s3Client;

    /**
     * Name of the S3 bucket where PDF files are stored.
     */
    @Value("${aws.s3.bucket}")
    private String bucket;

    /**
     * Retrieves a PDF file from Amazon S3.
     * <p>
     * The file is read entirely into memory and returned as a byte array,
     * typically to be included in a response or further processed.
     * </p>
     *
     * @param key the S3 object key that identifies the PDF file
     *            (e.g. {@code pdf/employee-report.pdf})
     * @return a byte array containing the PDF file content
     * @throws RuntimeException if an error occurs while reading the file from S3
     */
    @Override
    public byte[] getPdf(String key) {
        GetObjectRequest request = GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        try (ResponseInputStream<GetObjectResponse> s3Object =
                     s3Client.getObject(request)) {

            return s3Object.readAllBytes();

        } catch (IOException e) {
            throw new RuntimeException("Error reading PDF from S3", e);
        }
    }
}

