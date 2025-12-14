package co.parameta.technical.test.rest.service;

import co.parameta.technical.test.commons.dto.ResponseGeneralDTO;
import co.parameta.technical.test.commons.pojo.EmployeeResponsePojo;
import co.parameta.technical.test.commons.pojo.SaveEmployeeRequestPojo;
import co.parameta.technical.test.commons.service.IJwtService;
import co.parameta.technical.test.commons.util.mapper.ScriptValidationMapper;
import co.parameta.technical.test.rest.dto.EmployeeRequestDTO;
import co.parameta.technical.test.rest.dto.ResponseEmployeeDTO;
import co.parameta.technical.test.rest.dto.ResponseValidationGroovieDTO;
import co.parameta.technical.test.rest.repository.ScriptValidationRepository;
import co.parameta.technical.test.rest.service.impl.ValidationEmployeeService;
import co.parameta.technical.test.rest.util.mapper.JsonToPojoMapper;
import co.parameta.technical.test.rest.util.mapper.PojoToJsonMapper;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ws.client.core.WebServiceMessageCallback;
import org.springframework.ws.client.core.WebServiceTemplate;

import java.util.*;

import static java.net.HttpURLConnection.HTTP_INTERNAL_ERROR;
import static java.net.HttpURLConnection.HTTP_OK;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ValidationEmployeeServiceTest {

    @InjectMocks
    private ValidationEmployeeService service;

    @Mock
    private IGroovieScriptExecutorService groovyScriptExecutorService;

    @Mock
    private ScriptValidationRepository scriptValidationRepository;

    @Mock
    private ScriptValidationMapper scriptValidationMapper;

    @Mock
    private IJwtService jwtService;

    @Mock
    private WebServiceTemplate webServiceTemplate;

    @Mock
    private JsonToPojoMapper jsonToPojoMapper;

    @Mock
    private PojoToJsonMapper pojoToJsonMapper;

    @Mock
    private IPrepareMailDeliveryService prepareMailDeliveryService;

    @Test
    void validateEmployeeNullReturnsOkAndDoesNotInvokeDependencies()
            throws MessagingException {

        ResponseGeneralDTO resp = service.validationEmployee(null);

        assertNotNull(resp);
        assertEquals(HTTP_OK, resp.getStatus());

        verifyNoInteractions(
                groovyScriptExecutorService,
                scriptValidationRepository,
                scriptValidationMapper,
                jwtService,
                webServiceTemplate,
                jsonToPojoMapper,
                pojoToJsonMapper,
                prepareMailDeliveryService
        );
    }

    @Test
    void validateEmployeeGroovyValidationErrorReturns500AndDoesNotCallSoap()
            throws MessagingException {

        EmployeeRequestDTO req = new EmployeeRequestDTO();
        req.setNames("Uno");

        when(scriptValidationRepository.searchActiveValidationsGroovie())
                .thenReturn(Collections.emptyList());
        when(scriptValidationMapper.toListDto(any()))
                .thenReturn(Collections.emptyList());

        doAnswer(invocation -> {
            @SuppressWarnings("unchecked")
            Map<String, Object> extras = invocation.getArgument(1, Map.class);

            @SuppressWarnings("unchecked")
            List<ResponseValidationGroovieDTO> list =
                    (List<ResponseValidationGroovieDTO>) extras.get("listValidation");

            ResponseValidationGroovieDTO error = new ResponseValidationGroovieDTO();
            error.setError(true);
            error.setMessage("The person is a minor");
            list.add(error);

            return "ignored";
        }).when(groovyScriptExecutorService).runScript(any(), anyMap(), anyList());

        ResponseGeneralDTO resp = service.validationEmployee(req);

        assertNotNull(resp);
        assertEquals(HTTP_INTERNAL_ERROR, resp.getStatus());
        assertEquals("The person is a minor", resp.getMessage());

        verify(pojoToJsonMapper, never()).toResponseEmployeeDto(any(), any());
        verify(prepareMailDeliveryService, never()).prepareMailDelivery(any());
    }

    @Test
    void validateEmployeeSuccessSetsDataAndSendsEmailWhenEmailPresent()
            throws Exception {

        EmployeeRequestDTO req = new EmployeeRequestDTO();
        req.setNames("Uno");
        req.setLastNames("Dos");
        req.setEmail("uno@test.com");

        when(scriptValidationRepository.searchActiveValidationsGroovie())
                .thenReturn(Collections.emptyList());
        when(scriptValidationMapper.toListDto(any()))
                .thenReturn(Collections.emptyList());
        doAnswer(invocation -> "ok")
                .when(groovyScriptExecutorService).runScript(any(), anyMap(), anyList());

        when(jwtService.getTokenFromHeader()).thenReturn("jwt.header.token");
        when(jwtService.getCodeFromToken("jwt.header.token")).thenReturn("ADM001");

        SaveEmployeeRequestPojo saveRequestPojo = new SaveEmployeeRequestPojo();
        when(jsonToPojoMapper.toSaveEmployeeRequest(eq(req), eq("ADM001")))
                .thenReturn(saveRequestPojo);

        EmployeeResponsePojo employeeResponsePojo =
                mock(EmployeeResponsePojo.class, RETURNS_DEEP_STUBS);
        when(employeeResponsePojo.getResponse().getStatus()).thenReturn("200");
        when(employeeResponsePojo.getResponse().getMessage()).thenReturn("Saved");

        when(webServiceTemplate.marshalSendAndReceive(
                eq(saveRequestPojo), any(WebServiceMessageCallback.class)))
                .thenReturn(employeeResponsePojo);

        ResponseEmployeeDTO responseEmployeeDTO = new ResponseEmployeeDTO();
        when(pojoToJsonMapper.toResponseEmployeeDto(employeeResponsePojo, req))
                .thenReturn(responseEmployeeDTO);

        ResponseGeneralDTO resp = service.validationEmployee(req);

        assertNotNull(resp);
        assertEquals(200, resp.getStatus());
        assertEquals("Saved", resp.getMessage());
        assertSame(responseEmployeeDTO, resp.getData());

        verify(webServiceTemplate, times(1))
                .marshalSendAndReceive(eq(saveRequestPojo), any(WebServiceMessageCallback.class));
        verify(pojoToJsonMapper, times(1))
                .toResponseEmployeeDto(employeeResponsePojo, req);
        verify(prepareMailDeliveryService, times(1))
                .prepareMailDelivery(req);
    }

    @Test
    void validateEmployeeSoapErrorDoesNotSetDataButSendsEmailWhenEmailPresent()
            throws Exception {

        EmployeeRequestDTO req = new EmployeeRequestDTO();
        req.setEmail("uno@test.com");

        when(scriptValidationRepository.searchActiveValidationsGroovie())
                .thenReturn(Collections.emptyList());
        when(scriptValidationMapper.toListDto(any()))
                .thenReturn(Collections.emptyList());
        doAnswer(invocation -> "ok")
                .when(groovyScriptExecutorService).runScript(any(), anyMap(), anyList());

        when(jwtService.getTokenFromHeader()).thenReturn("jwt.header.token");
        when(jwtService.getCodeFromToken("jwt.header.token")).thenReturn("ADM001");

        SaveEmployeeRequestPojo saveRequestPojo = new SaveEmployeeRequestPojo();
        when(jsonToPojoMapper.toSaveEmployeeRequest(eq(req), eq("ADM001")))
                .thenReturn(saveRequestPojo);

        EmployeeResponsePojo employeeResponsePojo =
                mock(EmployeeResponsePojo.class, RETURNS_DEEP_STUBS);
        when(employeeResponsePojo.getResponse().getStatus()).thenReturn("500");
        when(employeeResponsePojo.getResponse().getMessage()).thenReturn("SOAP ERROR");

        when(webServiceTemplate.marshalSendAndReceive(
                eq(saveRequestPojo), any(WebServiceMessageCallback.class)))
                .thenReturn(employeeResponsePojo);

        ResponseGeneralDTO resp = service.validationEmployee(req);

        assertNotNull(resp);
        assertEquals(500, resp.getStatus());
        assertEquals("SOAP ERROR", resp.getMessage());
        assertNull(resp.getData());

        verify(pojoToJsonMapper, never()).toResponseEmployeeDto(any(), any());
        verify(prepareMailDeliveryService, times(1))
                .prepareMailDelivery(req);
    }

    @Test
    void validateEmployeeWithoutEmailDoesNotSendEmail() throws Exception {

        EmployeeRequestDTO req = new EmployeeRequestDTO();
        req.setEmail("   ");

        when(scriptValidationRepository.searchActiveValidationsGroovie())
                .thenReturn(Collections.emptyList());
        when(scriptValidationMapper.toListDto(any()))
                .thenReturn(Collections.emptyList());
        doAnswer(invocation -> "ok")
                .when(groovyScriptExecutorService).runScript(any(), anyMap(), anyList());

        when(jwtService.getTokenFromHeader()).thenReturn("jwt.header.token");
        when(jwtService.getCodeFromToken("jwt.header.token")).thenReturn("ADM001");

        SaveEmployeeRequestPojo saveRequestPojo = new SaveEmployeeRequestPojo();
        when(jsonToPojoMapper.toSaveEmployeeRequest(eq(req), eq("ADM001")))
                .thenReturn(saveRequestPojo);

        EmployeeResponsePojo employeeResponsePojo =
                mock(EmployeeResponsePojo.class, RETURNS_DEEP_STUBS);
        when(employeeResponsePojo.getResponse().getStatus()).thenReturn("200");
        when(employeeResponsePojo.getResponse().getMessage()).thenReturn("Saved");

        when(webServiceTemplate.marshalSendAndReceive(
                eq(saveRequestPojo), any(WebServiceMessageCallback.class)))
                .thenReturn(employeeResponsePojo);

        when(pojoToJsonMapper.toResponseEmployeeDto(employeeResponsePojo, req))
                .thenReturn(new ResponseEmployeeDTO());

        ResponseGeneralDTO resp = service.validationEmployee(req);

        assertEquals(200, resp.getStatus());
        verify(prepareMailDeliveryService, never()).prepareMailDelivery(any());
    }
}
