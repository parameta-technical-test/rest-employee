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
    private IGetPdfS3Service iGetPdfS3Service;

    @Mock
    private SystemParameterRepository systemParameterRepository;

    private SystemParameterEntity buildParam(String content) {
        SystemParameterEntity p = new SystemParameterEntity();
        p.setId(1);
        p.setName("GET_PDF_EMPLOYEE");
        p.setContent(content);
        p.setDateCreate(new Date());
        p.setDateUpdate(null);
        return p;
    }

    @Test
    void allInformationByIdWithPdfEnabledFetchesMapsAndLoadsPdf() {

        when(systemParameterRepository.findByName("GET_PDF_EMPLOYEE"))
                .thenReturn(buildParam("1"));

        EmployeeEntity entity = new EmployeeEntity();
        when(employeeRepository.searchAllInformationEmployee(eq(10), isNull(), isNull()))
                .thenReturn(entity);

        EmployeeDTO employeeDTO = new EmployeeDTO();
        employeeDTO.setDateOfBirth(new Date());
        employeeDTO.setDateAffiliationCompany(new Date());
        employeeDTO.setStorageLocationReport("pdf/report.pdf");

        when(employeeMapper.toDto(eq(entity))).thenReturn(employeeDTO);

        byte[] pdf = "PDF".getBytes();
        when(iGetPdfS3Service.getPdf("pdf/report.pdf")).thenReturn(pdf);

        AllInformationEmployeeDTO allInfo = new AllInformationEmployeeDTO();
        when(employeeMapper.employeeDTOToAllInformationEmployeeDTO(
                eq(employeeDTO),
                any(),
                any(),
                eq(pdf)
        )).thenReturn(allInfo);

        ResponseGeneralDTO response = service.allInformationEmployee(10, null, null);

        assertNotNull(response);
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals("The information was consulted correctly", response.getMessage());
        assertSame(allInfo, response.getData());

        verify(employeeRepository, times(1))
                .searchAllInformationEmployee(10, null, null);
        verify(employeeMapper, times(1)).toDto(entity);
        verify(iGetPdfS3Service, times(1)).getPdf("pdf/report.pdf");
    }

    @Test
    void allInformationByDocumentWithPdfEnabledReturnsOk() {

        when(systemParameterRepository.findByName("GET_PDF_EMPLOYEE"))
                .thenReturn(buildParam("1"));

        EmployeeEntity entity = new EmployeeEntity();

        when(employeeRepository.searchAllInformationEmployee(isNull(), eq("123"), eq("CC")))
                .thenReturn(entity);

        EmployeeDTO employeeDTO = new EmployeeDTO();
        employeeDTO.setDateOfBirth(new Date());
        employeeDTO.setDateAffiliationCompany(new Date());
        employeeDTO.setStorageLocationReport("pdf/doc.pdf");

        when(employeeMapper.toDto(eq(entity))).thenReturn(employeeDTO);

        byte[] pdf = "PDF2".getBytes();
        when(iGetPdfS3Service.getPdf("pdf/doc.pdf")).thenReturn(pdf);

        AllInformationEmployeeDTO allInfo = new AllInformationEmployeeDTO();
        when(employeeMapper.employeeDTOToAllInformationEmployeeDTO(
                eq(employeeDTO),
                any(),
                any(),
                eq(pdf)
        )).thenReturn(allInfo);

        ResponseGeneralDTO response = service.allInformationEmployee(null, "CC", "123");

        assertNotNull(response);
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals("The information was consulted correctly", response.getMessage());
        assertSame(allInfo, response.getData());

        verify(employeeRepository, times(1))
                .searchAllInformationEmployee(null, "123", "CC");
        verify(iGetPdfS3Service, times(1)).getPdf("pdf/doc.pdf");
    }

    @Test
    void allInformationWithPdfDisabledDoesNotCallS3AndPdfIsNull() {

        when(systemParameterRepository.findByName("GET_PDF_EMPLOYEE"))
                .thenReturn(buildParam("0"));

        EmployeeEntity entity = new EmployeeEntity();
        when(employeeRepository.searchAllInformationEmployee(eq(5), isNull(), isNull()))
                .thenReturn(entity);

        EmployeeDTO employeeDTO = new EmployeeDTO();
        employeeDTO.setDateOfBirth(new Date());
        employeeDTO.setDateAffiliationCompany(new Date());
        employeeDTO.setStorageLocationReport("pdf/x.pdf");

        when(employeeMapper.toDto(eq(entity))).thenReturn(employeeDTO);

        AllInformationEmployeeDTO allInfo = new AllInformationEmployeeDTO();
        when(employeeMapper.employeeDTOToAllInformationEmployeeDTO(
                eq(employeeDTO),
                any(),
                any(),
                isNull()
        )).thenReturn(allInfo);

        ResponseGeneralDTO response = service.allInformationEmployee(5, null, null);

        assertNotNull(response);
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals("The information was consulted correctly", response.getMessage());
        assertSame(allInfo, response.getData());

        verify(iGetPdfS3Service, never()).getPdf(anyString());
    }

    @Test
    void allInformationWithoutIdOrDocumentReturnsNullData() {

        when(systemParameterRepository.findByName("GET_PDF_EMPLOYEE"))
                .thenReturn(buildParam("1"));

        ResponseGeneralDTO response = service.allInformationEmployee(null, null, null);

        assertNotNull(response);
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals("The information was consulted correctly", response.getMessage());
        assertNull(response.getData());

        verify(employeeRepository, never()).searchAllInformationEmployee(any(), any(), any());
        verify(employeeMapper, never()).toDto(any());
        verify(iGetPdfS3Service, never()).getPdf(anyString());
    }
}
