package co.parameta.technical.test.rest.service.impl;

import co.parameta.technical.test.commons.dto.ResponseGeneralDTO;
import co.parameta.technical.test.commons.pojo.EmployeeResponsePojo;
import co.parameta.technical.test.commons.service.IJwtService;
import co.parameta.technical.test.commons.util.helper.GeneralUtil;
import co.parameta.technical.test.commons.util.mapper.ScriptValidationMapper;
import co.parameta.technical.test.rest.dto.EmployeeRequestDTO;
import co.parameta.technical.test.rest.dto.ResponseEmployeeDTO;
import co.parameta.technical.test.rest.dto.ResponseValidationGroovieDTO;
import co.parameta.technical.test.rest.repository.PositionRepository;
import co.parameta.technical.test.rest.repository.ScriptValidationRepository;
import co.parameta.technical.test.rest.repository.TypeDocumentRepository;
import co.parameta.technical.test.rest.service.*;
import co.parameta.technical.test.rest.util.helper.GeneralRestUtil;
import co.parameta.technical.test.rest.util.mapper.JsonToPojoMapper;
import co.parameta.technical.test.rest.util.mapper.PojoToJsonMapper;
import co.parameta.technical.test.rest.util.mapper.PositionMapper;
import co.parameta.technical.test.rest.util.mapper.TypeDocumentMapper;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.transport.context.TransportContextHolder;
import org.springframework.ws.transport.http.HttpUrlConnection;

import java.util.*;

import static java.net.HttpURLConnection.HTTP_INTERNAL_ERROR;
import static java.net.HttpURLConnection.HTTP_OK;

/**
 * Service responsible for validating, registering and processing employees.
 * <p>
 * This service executes Groovy-based validations, invokes the SOAP employee
 * service, maps responses to REST DTOs and optionally triggers email delivery.
 */
@Service
@RequiredArgsConstructor
public class ValidationEmployeeService implements IValidationEmployeeService {

    private final IGroovieScriptExecutorService groovyScriptExecutorService;
    private final ScriptValidationRepository scriptValidationRepository;
    private final ScriptValidationMapper scriptValidationMapper;
    private final IJwtService jwtService;
    private final WebServiceTemplate webServiceTemplate;
    private final JsonToPojoMapper jsonToPojoMapper;
    private final PojoToJsonMapper pojoToJsonMapper;
    private final IPrepareMailDeliveryService iPrepareMailDeliveryService;
    private final TypeDocumentRepository typeDocumentRepository;
    private final TypeDocumentMapper typeDocumentMapper;
    private final PositionRepository positionRepository;
    private final PositionMapper positionMapper;

    /**
     * Validates and processes an employee request.
     * <p>
     * The flow includes:
     * <ul>
     *     <li>Groovy validation execution</li>
     *     <li>SOAP service invocation</li>
     *     <li>Response mapping to REST DTO</li>
     *     <li>Optional email notification</li>
     * </ul>
     *
     * @param employeeRequest employee data to validate and register
     * @return a {@link ResponseGeneralDTO} with the process result
     * @throws MessagingException if email delivery fails
     */
    @Override
    public ResponseGeneralDTO validationEmployee(EmployeeRequestDTO employeeRequest)
            throws MessagingException {

        ResponseGeneralDTO response = new ResponseGeneralDTO();
        response.setStatus(HTTP_OK);

        if (employeeRequest == null) {
            return response;
        }

        List<ResponseValidationGroovieDTO> validationResults = new ArrayList<>();
        Map<String, Object> extraValues = new HashMap<>();

        extraValues.put("generalUtilRest", GeneralRestUtil.class);
        extraValues.put("generalUtil", GeneralUtil.class);
        extraValues.put("listValidation", validationResults);

        groovyScriptExecutorService.runScript(
                employeeRequest,
                extraValues,
                scriptValidationMapper.toListDto(
                        scriptValidationRepository.searchActiveValidationsGroovie()
                )
        );

        for (ResponseValidationGroovieDTO validation : validationResults) {
            if (validation.isError()) {
                response.setStatus(HTTP_INTERNAL_ERROR);
                response.setMessage(validation.getMessage());
                return response;
            }
        }

        EmployeeResponsePojo employeeResponse =
                (EmployeeResponsePojo) webServiceTemplate.marshalSendAndReceive(
                        jsonToPojoMapper.toSaveEmployeeRequest(
                                employeeRequest,
                                jwtService.getCodeFromToken(jwtService.getTokenFromHeader())
                        ),
                        message -> {
                            var transportContext = TransportContextHolder.getTransportContext();
                            var connection = (HttpUrlConnection) transportContext.getConnection();
                            connection.addRequestHeader(
                                    "Authorization",
                                    "Bearer " + jwtService.getTokenFromHeader()
                            );
                        }
                );

        int status = GeneralUtil.mapToValueObject(
                GeneralUtil.get(
                        () -> employeeResponse.getResponse().getStatus(),
                        null
                ),
                Integer.class,
                null
        );

        if (status != HTTP_INTERNAL_ERROR) {

            ResponseEmployeeDTO responseEmployee =
                    pojoToJsonMapper.toResponseEmployeeDto(
                            employeeResponse,
                            employeeRequest,
                            typeDocumentMapper.toDto(typeDocumentRepository.documentInformation(employeeRequest.getTypeDocument())),
                            positionMapper.toDto(positionRepository.postionInformation(employeeRequest.getPosition()))
                    );
            response.setData(responseEmployee);
        }

        if (!GeneralRestUtil.isNullOrBlank(employeeRequest.getEmail())) {
            iPrepareMailDeliveryService.prepareMailDelivery(employeeRequest, status == 200);
        }

        response.setStatus(status);
        response.setMessage(
                GeneralUtil.get(
                        () -> employeeResponse.getResponse().getMessage(),
                        null
                )
        );

        return response;
    }
}
