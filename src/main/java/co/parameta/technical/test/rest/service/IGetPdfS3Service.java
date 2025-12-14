package co.parameta.technical.test.rest.service;

/**
 * Service interface responsible for retrieving PDF files from Amazon S3.
 * <p>
 * Implementations of this interface must download a PDF file using the
 * provided storage key and return its content as a byte array.
 * </p>
 */
public interface IGetPdfS3Service {

     /**
      * Retrieves a PDF file from S3 using the given storage key.
      *
      * @param key the S3 object key that identifies the PDF file
      *            (e.g. {@code pdf/employee-report.pdf})
      * @return a byte array containing the PDF file content
      * @throws RuntimeException if the PDF cannot be retrieved or read
      */
     byte[] getPdf(String key);

}
