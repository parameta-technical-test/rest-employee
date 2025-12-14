package co.parameta.technical.test.rest.service;

import co.parameta.technical.test.commons.dto.EmployeeDTO;
import co.parameta.technical.test.commons.dto.ResponseGeneralDTO;
import co.parameta.technical.test.commons.entity.EmployeeEntity;
import co.parameta.technical.test.commons.entity.SystemParameterEntity;
import co.parameta.technical.test.rest.dto.AllInformationEmployeeDTO;
import co.parameta.technical.test.rest.repository.EmployeeRepository;
import co.parameta.technical.test.rest.repository.SystemParameterRepository;
import co.parameta.technical.test.rest.service.impl.ViewAllUserInformationService;
import co.parameta.technical.test.rest.util.mapper.EmployeeMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ViewAllUserInformationServiceTest {

    @InjectMocks
    private ViewAllUserInformationService service;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private EmployeeMapper employeeMapper;

    @Mock
    private IGetPdfS3Service pdfS3Service;

    @Mock
    private SystemParameterRepository systemParameterRepository;

    private SystemParameterEntity buildParam(String content) {
        return new SystemParameterEntity(
                1,
                "GET_PDF_EMPLOYEE",
                content,
                "SYSTEM",
                new Date(),
                null
        );
    }

    @Test
    void getAllInformationByIdWithPdfEnabledFetchesMapsAndLoadsPdf() {
        when(systemParameterRepository.findByName("GET_PDF_EMPLOYEE"))
                .thenReturn(buildParam("1"));

        EmployeeEntity entity = new EmployeeEntity();
        when(employeeRepository.searchAllInformationEmployee(eq(10), isNull(), isNull()))
                .thenReturn(entity);

        EmployeeDTO employeeDTO = new EmployeeDTO();
        employeeDTO.setDateOfBirth(new Date());
        employeeDTO.setDateAffiliationCompany(new Date());
        employeeDTO.setStorageLocationReport("pdf/report.pdf");

        when(employeeMapper.toDto(entity)).thenReturn(employeeDTO);

        byte[] pdf = "PDF".getBytes();
        when(pdfS3Service.getPdf("pdf/report.pdf")).thenReturn(pdf);

        AllInformationEmployeeDTO allInfo = new AllInformationEmployeeDTO();
        when(employeeMapper.employeeDTOToAllInformationEmployeeDTO(
                eq(employeeDTO),
                any(),
                any(),
                eq(pdf)
        )).thenReturn(allInfo);

        ResponseGeneralDTO response =
                service.allInformationEmployee(10, null, null);

        assertNotNull(response);
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals("Se consulto correctamente la informacion", response.getMessage());
        assertSame(allInfo, response.getData());

        verify(employeeRepository, times(1))
                .searchAllInformationEmployee(10, null, null);
        verify(employeeMapper, times(1)).toDto(entity);
        verify(pdfS3Service, times(1)).getPdf("pdf/report.pdf");
    }

    @Test
    void getAllInformationByDocumentWithPdfEnabledReturnsOk() {
        when(systemParameterRepository.findByName("GET_PDF_EMPLOYEE"))
                .thenReturn(buildParam("1"));

        EmployeeEntity entity = new EmployeeEntity();
        when(employeeRepository.searchAllInformationEmployee(isNull(), eq("CC"), eq("123")))
                .thenReturn(entity);

        EmployeeDTO employeeDTO = new EmployeeDTO();
        employeeDTO.setDateOfBirth(new Date());
        employeeDTO.setDateAffiliationCompany(new Date());
        employeeDTO.setStorageLocationReport("pdf/doc.pdf");

        when(employeeMapper.toDto(entity)).thenReturn(employeeDTO);

        byte[] pdf = "PDF2".getBytes();
        when(pdfS3Service.getPdf("pdf/doc.pdf")).thenReturn(pdf);

        AllInformationEmployeeDTO allInfo = new AllInformationEmployeeDTO();
        when(employeeMapper.employeeDTOToAllInformationEmployeeDTO(
                eq(employeeDTO),
                any(),
                any(),
                eq(pdf)
        )).thenReturn(allInfo);

        ResponseGeneralDTO response =
                service.allInformationEmployee(null, "CC", "123");

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertSame(allInfo, response.getData());

        verify(employeeRepository, times(1))
                .searchAllInformationEmployee(null, "CC", "123");
        verify(pdfS3Service, times(1)).getPdf("pdf/doc.pdf");
    }

    @Test
    void getAllInformationWithPdfDisabledDoesNotCallS3AndPdfIsNull() {
        when(systemParameterRepository.findByName("GET_PDF_EMPLOYEE"))
                .thenReturn(buildParam("0"));

        EmployeeEntity entity = new EmployeeEntity();
        when(employeeRepository.searchAllInformationEmployee(eq(5), isNull(), isNull()))
                .thenReturn(entity);

        EmployeeDTO employeeDTO = new EmployeeDTO();
        employeeDTO.setDateOfBirth(new Date());
        employeeDTO.setDateAffiliationCompany(new Date());
        employeeDTO.setStorageLocationReport("pdf/x.pdf");

        when(employeeMapper.toDto(entity)).thenReturn(employeeDTO);

        AllInformationEmployeeDTO allInfo = new AllInformationEmployeeDTO();
        when(employeeMapper.employeeDTOToAllInformationEmployeeDTO(
                eq(employeeDTO),
                any(),
                any(),
                isNull()
        )).thenReturn(allInfo);

        ResponseGeneralDTO response =
                service.allInformationEmployee(5, null, null);

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertSame(allInfo, response.getData());

        verify(pdfS3Service, never()).getPdf(anyString());
    }

    @Test
    void getAllInformationWithoutIdOrDocumentReturnsNullData() {
        when(systemParameterRepository.findByName("GET_PDF_EMPLOYEE"))
                .thenReturn(buildParam("1"));

        ResponseGeneralDTO response =
                service.allInformationEmployee(null, null, null);

        assertNotNull(response);
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals("Se consulto correctamente la informacion", response.getMessage());
        assertNull(response.getData());

        verify(employeeRepository, never()).searchAllInformationEmployee(any(), any(), any());
        verify(employeeMapper, never()).toDto(any());
        verify(pdfS3Service, never()).getPdf(anyString());
    }
}