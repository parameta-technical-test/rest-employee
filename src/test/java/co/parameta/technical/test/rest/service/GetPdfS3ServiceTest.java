package co.parameta.technical.test.rest.service;

import co.parameta.technical.test.rest.service.impl.GetPdfS3Service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetPdfS3ServiceTest {

    @InjectMocks
    private GetPdfS3Service getPdfS3Service;

    @Mock
    private S3Client s3Client;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(getPdfS3Service, "bucket", "test-bucket");
    }

    @Test
    void getPdfReturnsBytes() throws IOException {
        String key = "employees/test.pdf";
        byte[] expectedBytes = "PDF_CONTENT".getBytes();

        GetObjectResponse response = GetObjectResponse.builder().build();
        ResponseInputStream<GetObjectResponse> responseInputStream =
                new ResponseInputStream<>(
                        response,
                        new ByteArrayInputStream(expectedBytes)
                );

        when(s3Client.getObject(any(GetObjectRequest.class)))
                .thenReturn(responseInputStream);

        byte[] result = getPdfS3Service.getPdf(key);

        assertNotNull(result);
        assertArrayEquals(expectedBytes, result);

        verify(s3Client, times(1))
                .getObject(any(GetObjectRequest.class));
    }
}
