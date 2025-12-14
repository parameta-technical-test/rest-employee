package co.parameta.technical.test.rest.service;

/**
 * Service interface responsible for storing PDF files in Amazon S3.
 * <p>
 * Implementations of this interface must upload employee PDF reports
 * to an S3 bucket and return the generated storage key.
 * </p>
 */
public interface IS3PdfStorageService {

    /**
     * Uploads a PDF file to Amazon S3 and associates it with an employee.
     * <p>
     * If the original filename is {@code null} or blank, a default filename
     * will be used.
     * </p>
     *
     * @param pdfBytes         the PDF file content as a byte array
     * @param originalFilename the original filename of the PDF (optional)
     * @param documentNumber   the employee document number
     * @param typeDocument     the employee document type
     * @return the generated S3 object key where the PDF was stored
     *         (e.g. {@code pdf/PDF-ABC123.pdf})
     * @throws RuntimeException if the upload fails or storage cannot be completed
     */
    String uploadPdf(
            byte[] pdfBytes,
            String originalFilename,
            String documentNumber,
            String typeDocument
    );

}
