package co.parameta.technical.test.rest.service;

public interface IS3PdfStorageService {

    String uploadPdf(byte[] pdfBytes, String originalFilename, String documentNumber, String typeDocument);

}
