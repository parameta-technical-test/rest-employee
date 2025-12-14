package co.parameta.technical.test.rest.service;

import co.parameta.technical.test.commons.dto.PositionDTO;
import co.parameta.technical.test.commons.dto.ResponseGeneralDTO;
import co.parameta.technical.test.commons.dto.TypeDocumentDTO;
import co.parameta.technical.test.commons.entity.PositionEntity;
import co.parameta.technical.test.commons.entity.TypeDocumentEntity;
import co.parameta.technical.test.commons.pojo.EmployeeResponsePojo;
import co.parameta.technical.test.commons.pojo.SaveEmployeeRequestPojo;
import co.parameta.technical.test.commons.service.IJwtService;
import co.parameta.technical.test.commons.util.mapper.ScriptValidationMapper;
import co.parameta.technical.test.rest.dto.EmployeeRequestDTO;
import co.parameta.technical.test.rest.dto.ResponseEmployeeDTO;
import co.parameta.technical.test.rest.dto.ResponseValidationGroovieDTO;
import co.parameta.technical.test.rest.repository.PositionRepository;
import co.parameta.technical.test.rest.repository.ScriptValidationRepository;
import co.parameta.technical.test.rest.repository.TypeDocumentRepository;
import co.parameta.technical.test.rest.service.impl.ValidationEmployeeService;
import co.parameta.technical.test.rest.util.mapper.JsonToPojoMapper;
import co.parameta.technical.test.rest.util.mapper.PojoToJsonMapper;
import co.parameta.technical.test.rest.util.mapper.PositionMapper;
import co.parameta.technical.test.rest.util.mapper.TypeDocumentMapper;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.client.core.WebServiceMessageCallback;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.transport.context.TransportContext;
import org.springframework.ws.transport.context.TransportContextHolder;
import org.springframework.ws.transport.http.HttpUrlConnection;

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
    private IPrepareMailDeliveryService iPrepareMailDeliveryService;

    @Mock
    private TypeDocumentRepository typeDocumentRepository;

    @Mock
    private TypeDocumentMapper typeDocumentMapper;

    @Mock
    private PositionRepository positionRepository;

    @Mock
    private PositionMapper positionMapper;

    @Mock
    private TransportContext transportContext;

    @Mock
    private HttpUrlConnection httpUrlConnection;

    @BeforeEach
    void setUpTransportContext() {
        TransportContextHolder.setTransportContext(transportContext);
    }

    @AfterEach
    void clearTransportContext() {
        TransportContextHolder.setTransportContext(null);
    }

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
                iPrepareMailDeliveryService,
                typeDocumentRepository,
                typeDocumentMapper,
                positionRepository,
                positionMapper
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

            return null;
        }).when(groovyScriptExecutorService).runScript(any(), anyMap(), anyList());

        ResponseGeneralDTO resp = service.validationEmployee(req);

        assertNotNull(resp);
        assertEquals(HTTP_INTERNAL_ERROR, resp.getStatus());
        assertEquals("The person is a minor", resp.getMessage());

        verify(webServiceTemplate, never())
                .marshalSendAndReceive(any(), (Object) any(WebServiceMessageCallback.class));

        verify(jwtService, never()).getTokenFromHeader();
        verify(jwtService, never()).getCodeFromToken(anyString());
        verify(jsonToPojoMapper, never()).toSaveEmployeeRequest(any(), anyString());
        verify(pojoToJsonMapper, never()).toResponseEmployeeDto(any(), any(), any(), any());
        verify(iPrepareMailDeliveryService, never()).prepareMailDelivery(any(), anyBoolean());
        verifyNoInteractions(typeDocumentRepository, typeDocumentMapper, positionRepository, positionMapper);
    }

    @Test
    void validateEmployeeSuccessSetsDataAndSendsEmailWhenEmailPresent()
            throws Exception {

        when(transportContext.getConnection()).thenReturn(httpUrlConnection);

        EmployeeRequestDTO req = new EmployeeRequestDTO();
        req.setNames("Uno");
        req.setLastNames("Dos");
        req.setTypeDocument("CC");
        req.setPosition("DEV");
        req.setEmail("uno@test.com");

        when(scriptValidationRepository.searchActiveValidationsGroovie())
                .thenReturn(Collections.emptyList());
        when(scriptValidationMapper.toListDto(any()))
                .thenReturn(Collections.emptyList());

        doAnswer(invocation -> null)
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

        TypeDocumentEntity typeDocEntity = new TypeDocumentEntity();
        PositionEntity posEntity = new PositionEntity();
        TypeDocumentDTO typeDocDto = new TypeDocumentDTO();
        PositionDTO posDto = new PositionDTO();

        when(typeDocumentRepository.documentInformation("CC")).thenReturn(typeDocEntity);
        when(positionRepository.postionInformation("DEV")).thenReturn(posEntity);

        when(typeDocumentMapper.toDto(typeDocEntity)).thenReturn(typeDocDto);
        when(positionMapper.toDto(posEntity)).thenReturn(posDto);

        doAnswer(inv -> {
            WebServiceMessageCallback cb = inv.getArgument(1, WebServiceMessageCallback.class);
            cb.doWithMessage(mock(WebServiceMessage.class));
            return employeeResponsePojo;
        }).when(webServiceTemplate).marshalSendAndReceive(eq(saveRequestPojo), any(WebServiceMessageCallback.class));

        ResponseEmployeeDTO responseEmployeeDTO = new ResponseEmployeeDTO();
        when(pojoToJsonMapper.toResponseEmployeeDto(employeeResponsePojo, req, typeDocDto, posDto))
                .thenReturn(responseEmployeeDTO);

        ResponseGeneralDTO resp = service.validationEmployee(req);

        assertNotNull(resp);
        assertEquals(200, resp.getStatus());
        assertEquals("Saved", resp.getMessage());
        assertSame(responseEmployeeDTO, resp.getData());

        verify(webServiceTemplate, times(1))
                .marshalSendAndReceive(eq(saveRequestPojo), any(WebServiceMessageCallback.class));

        verify(httpUrlConnection, times(1))
                .addRequestHeader("Authorization", "Bearer jwt.header.token");

        verify(typeDocumentRepository, times(1)).documentInformation("CC");
        verify(positionRepository, times(1)).postionInformation("DEV");
        verify(typeDocumentMapper, times(1)).toDto(typeDocEntity);
        verify(positionMapper, times(1)).toDto(posEntity);

        verify(pojoToJsonMapper, times(1))
                .toResponseEmployeeDto(employeeResponsePojo, req, typeDocDto, posDto);

        verify(iPrepareMailDeliveryService, times(1))
                .prepareMailDelivery(req, true);
    }

    @Test
    void validateEmployeeSoapErrorDoesNotSetDataButSendsEmailWhenEmailPresent()
            throws Exception {

        when(transportContext.getConnection()).thenReturn(httpUrlConnection);

        EmployeeRequestDTO req = new EmployeeRequestDTO();
        req.setTypeDocument("CC");
        req.setPosition("DEV");
        req.setEmail("uno@test.com");

        when(scriptValidationRepository.searchActiveValidationsGroovie())
                .thenReturn(Collections.emptyList());
        when(scriptValidationMapper.toListDto(any()))
                .thenReturn(Collections.emptyList());

        doAnswer(invocation -> null)
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

        doAnswer(inv -> {
            WebServiceMessageCallback cb = inv.getArgument(1, WebServiceMessageCallback.class);
            cb.doWithMessage(mock(WebServiceMessage.class));
            return employeeResponsePojo;
        }).when(webServiceTemplate).marshalSendAndReceive(eq(saveRequestPojo), any(WebServiceMessageCallback.class));

        ResponseGeneralDTO resp = service.validationEmployee(req);

        assertNotNull(resp);
        assertEquals(500, resp.getStatus());
        assertEquals("SOAP ERROR", resp.getMessage());
        assertNull(resp.getData());

        verify(httpUrlConnection, times(1))
                .addRequestHeader("Authorization", "Bearer jwt.header.token");

        verify(pojoToJsonMapper, never()).toResponseEmployeeDto(any(), any(), any(), any());

        verifyNoInteractions(typeDocumentRepository, typeDocumentMapper, positionRepository, positionMapper);

        verify(iPrepareMailDeliveryService, times(1))
                .prepareMailDelivery(req, false);
    }

    @Test
    void validateEmployeeWithoutEmailDoesNotSendEmail()
            throws Exception {

        when(transportContext.getConnection()).thenReturn(httpUrlConnection);

        EmployeeRequestDTO req = new EmployeeRequestDTO();
        req.setTypeDocument("CC");
        req.setPosition("DEV");
        req.setEmail("   ");

        when(scriptValidationRepository.searchActiveValidationsGroovie())
                .thenReturn(Collections.emptyList());
        when(scriptValidationMapper.toListDto(any()))
                .thenReturn(Collections.emptyList());

        doAnswer(invocation -> null)
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

        TypeDocumentEntity typeDocEntity = new TypeDocumentEntity();
        PositionEntity posEntity = new PositionEntity();
        TypeDocumentDTO typeDocDto = new TypeDocumentDTO();
        PositionDTO posDto = new PositionDTO();

        when(typeDocumentRepository.documentInformation("CC")).thenReturn(typeDocEntity);
        when(positionRepository.postionInformation("DEV")).thenReturn(posEntity);

        when(typeDocumentMapper.toDto(typeDocEntity)).thenReturn(typeDocDto);
        when(positionMapper.toDto(posEntity)).thenReturn(posDto);

        doAnswer(inv -> {
            WebServiceMessageCallback cb = inv.getArgument(1, WebServiceMessageCallback.class);
            cb.doWithMessage(mock(WebServiceMessage.class));
            return employeeResponsePojo;
        }).when(webServiceTemplate).marshalSendAndReceive(eq(saveRequestPojo), any(WebServiceMessageCallback.class));

        when(pojoToJsonMapper.toResponseEmployeeDto(employeeResponsePojo, req, typeDocDto, posDto))
                .thenReturn(new ResponseEmployeeDTO());

        ResponseGeneralDTO resp = service.validationEmployee(req);

        assertEquals(200, resp.getStatus());
        assertEquals("Saved", resp.getMessage());

        verify(iPrepareMailDeliveryService, never())
                .prepareMailDelivery(any(), anyBoolean());
    }
}
