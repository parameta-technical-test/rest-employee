package co.parameta.technical.test.rest.service.impl;

import co.parameta.technical.test.commons.dto.ResponseGeneralDTO;
import co.parameta.technical.test.commons.pojo.EmployeeResponsePojo;
import co.parameta.technical.test.commons.service.IJwtService;
import co.parameta.technical.test.commons.util.helper.GeneralUtil;
import co.parameta.technical.test.commons.util.mapper.ScriptValidationMapper;
import co.parameta.technical.test.rest.dto.EmployeeRequestDTO;
import co.parameta.technical.test.rest.dto.ResponseEmployeeDTO;
import co.parameta.technical.test.rest.dto.ResponseValidationGroovieDTO;
import co.parameta.technical.test.rest.repository.EmployeeRepository;
import co.parameta.technical.test.rest.repository.ScriptValidationRepository;
import co.parameta.technical.test.rest.service.IGroovieScriptExecutorService;
import co.parameta.technical.test.rest.service.IValidationEmployeeService;
import co.parameta.technical.test.rest.util.helper.GeneralRestUtil;
import co.parameta.technical.test.rest.util.mapper.EmployeeMapper;
import co.parameta.technical.test.rest.util.mapper.JsonToPojoMapper;
import co.parameta.technical.test.rest.util.mapper.PojoToJsonMapper;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.ws.client.core.WebServiceTemplate;

import java.util.*;

import static co.parameta.technical.test.rest.util.constant.Constants.EMPLOYEE_SAVED_SUCCESSFULLY;
import static java.net.HttpURLConnection.HTTP_INTERNAL_ERROR;
import static java.net.HttpURLConnection.HTTP_OK;

@Service
@AllArgsConstructor
public class ValidationEmployeeService implements IValidationEmployeeService {

    private final IGroovieScriptExecutorService groovyScriptExecutorService;

    @Qualifier("rest-script")
    private final ScriptValidationRepository scriptValidationRepository;

    private final ScriptValidationMapper scriptValidationMapper;
    private final IJwtService jwtService;
    private final WebServiceTemplate webServiceTemplate;
    private final JsonToPojoMapper jsonToPojoMapper;
    private final PojoToJsonMapper pojoToJsonMapper;
    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;

    @Override
    public ResponseGeneralDTO validationEmployee(EmployeeRequestDTO employeeRequest) {

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
                        )
                );

        ResponseEmployeeDTO responseEmployee =
                pojoToJsonMapper.toResponseEmployeeDto(
                        employeeResponse,
                        employeeMapper.toDto(
                                employeeRepository.findByTypeDocument_codeAndNumberDocument(
                                        employeeRequest.getTypeDocument(),
                                        employeeRequest.getDocumentNumber()
                                )
                        )
                );

        response.setStatus(HTTP_OK);
        response.setMessage(EMPLOYEE_SAVED_SUCCESSFULLY);
        response.setData(responseEmployee);

        return response;
    }
}
