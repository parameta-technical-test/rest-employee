package co.parameta.technical.test.rest.service;

import co.parameta.technical.test.rest.dto.EmployeeRequestDTO;
import co.parameta.technical.test.rest.service.impl.EmployeePdfGeneratorService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class EmployeePdfGeneratorServiceTest {

    private final EmployeePdfGeneratorService service =
            new EmployeePdfGeneratorService();

    @Test
    void generateEmployeeReportReturnsPdfBytes() {
        EmployeeRequestDTO dto = new EmployeeRequestDTO();
        dto.setNames("Brahian");
        dto.setLastNames("Caceres");
        dto.setTypeDocument("CC");
        dto.setDocumentNumber("1111");
        dto.setDateOfBirth("2000-11-02");
        dto.setDateAffiliationCompany("2020-12-01");
        dto.setPosition("DEV");
        dto.setEmail("uno@gmail.com");
        dto.setSalary(new BigDecimal("2500000"));

        byte[] pdfBytes = service.generateEmployeeReport(dto);

        assertNotNull(pdfBytes);
        assertTrue(pdfBytes.length > 0, "PDF content should not be empty");

        String header = new String(pdfBytes, 0, Math.min(pdfBytes.length, 4));
        assertEquals("%PDF", header, "Generated content is not a valid PDF");
    }

    @Test
    void generateEmployeeReportNullEmployeeThrowsIllegalArgumentException() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> service.generateEmployeeReport(null)
        );
        assertEquals("EmployeeRequestDTO cannot be null", ex.getMessage());
    }
}
