package co.parameta.technical.test.rest.service;

import co.parameta.technical.test.rest.dto.EmployeeRequestDTO;
import co.parameta.technical.test.rest.repository.PositionRepository;
import co.parameta.technical.test.rest.repository.TypeDocumentRepository;
import co.parameta.technical.test.rest.service.impl.EmployeePdfGeneratorService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmployeePdfGeneratorServiceTest {

    @Mock
    private PositionRepository positionRepository;

    @Mock
    private TypeDocumentRepository typeDocumentRepository;

    @InjectMocks
    private EmployeePdfGeneratorService service;

    @Test
    void generateEmployeeReportReturnsPdfBytes() {

        when(typeDocumentRepository.documentDescription(anyString()))
                .thenReturn("Citizenship Card");
        when(positionRepository.positionDescription(anyString()))
                .thenReturn("Developer");

        EmployeeRequestDTO dto = new EmployeeRequestDTO();
        dto.setNames("Brahian");
        dto.setLastNames("Caceres");
        dto.setTypeDocument("CC");
        dto.setDocumentNumber("1111");

        dto.setDateOfBirth("2000-11-02");

        dto.setDateAffiliationCompany("2020-12-01");

        dto.setPosition("DEV");
        dto.setEmail("uno@gmail.com");
        dto.setSalary("2500000");

        byte[] pdfBytes = service.generateEmployeeReport(dto, true);

        assertNotNull(pdfBytes);
        assertTrue(pdfBytes.length > 0);

        String header = new String(pdfBytes, 0, Math.min(pdfBytes.length, 4));
        assertEquals("%PDF", header);
    }

    @Test
    void generateEmployeeReportNullEmployeeThrowsIllegalArgumentException() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> service.generateEmployeeReport(null, true)
        );
        assertEquals("EmployeeRequestDTO cannot be null", ex.getMessage());
    }
}
