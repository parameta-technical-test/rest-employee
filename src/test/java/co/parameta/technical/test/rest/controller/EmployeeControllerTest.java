package co.parameta.technical.test.rest.controller;

import co.parameta.technical.test.commons.dto.ResponseGeneralDTO;
import co.parameta.technical.test.rest.dto.EmployeeRequestDTO;
import co.parameta.technical.test.rest.service.IValidationEmployeeService;
import co.parameta.technical.test.rest.service.IViewAllUserInformationService;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmployeeControllerTest {

    @InjectMocks
    private EmployeeController employeeController;

    @Mock
    private IValidationEmployeeService validationEmployeeService;

    @Mock
    private IViewAllUserInformationService viewAllUserInformationService;

    @Test
    void saveEmployeeSuccess() throws MessagingException {

        ResponseGeneralDTO responseDto = new ResponseGeneralDTO();
        responseDto.setStatus(HttpStatus.OK.value());
        responseDto.setMessage("Employee saved successfully");

        EmployeeRequestDTO request = new EmployeeRequestDTO();
        request.setNames("Uno");
        request.setLastNames("Dos");
        request.setTypeDocument("CC");
        request.setDocumentNumber("1111");
        request.setDateOfBirth("2000-11-02T00:00:00");
        request.setDateAffiliationCompany("2020-12-01T00:00:00");

        when(validationEmployeeService.validationEmployee(any(EmployeeRequestDTO.class)))
                .thenReturn(responseDto);

        ResponseEntity<ResponseGeneralDTO> response =
                employeeController.employeeSave(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(HttpStatus.OK.value(), response.getBody().getStatus());
        assertEquals("Employee saved successfully", response.getBody().getMessage());
    }

    @Test
    void getAllUserInformationSuccess() {

        ResponseGeneralDTO responseDto = new ResponseGeneralDTO();
        responseDto.setStatus(HttpStatus.OK.value());
        responseDto.setMessage("OK");

        Integer employeeId = 10;
        String documentType = "CC";
        String documentNumber = "15";

        when(viewAllUserInformationService.allInformationEmployee(
                eq(employeeId), eq(documentType), eq(documentNumber)))
                .thenReturn(responseDto);

        ResponseEntity<ResponseGeneralDTO> response =
                employeeController.allUserInformation(
                        documentType, documentNumber, employeeId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(HttpStatus.OK.value(), response.getBody().getStatus());
        assertEquals("OK", response.getBody().getMessage());
    }

    @Test
    void getAllUserInformationWithNullsSuccess() {

        ResponseGeneralDTO responseDto = new ResponseGeneralDTO();
        responseDto.setStatus(HttpStatus.OK.value());
        responseDto.setMessage("OK");

        when(viewAllUserInformationService.allInformationEmployee(
                eq(null), eq(null), eq(null)))
                .thenReturn(responseDto);

        ResponseEntity<ResponseGeneralDTO> response =
                employeeController.allUserInformation(null, null, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(HttpStatus.OK.value(), response.getBody().getStatus());
        assertEquals("OK", response.getBody().getMessage());
    }
}
