package co.parameta.tecnical.test.rest.service.impl;

import co.parameta.tecnical.test.commons.dto.ResponseGeneralDTO;
import co.parameta.tecnical.test.commons.util.helper.GeneralUtil;
import co.parameta.tecnical.test.commons.util.mapper.ScriptValidationMapper;
import co.parameta.tecnical.test.rest.dto.EmployeeRequestDTO;
import co.parameta.tecnical.test.rest.dto.ResponseValidationGroovieDTO;
import co.parameta.tecnical.test.rest.repository.ScriptValidationRepository;
import co.parameta.tecnical.test.rest.service.IGroovieScriptExecutorService;
import co.parameta.tecnical.test.rest.service.IValidationEmployeeService;
import co.parameta.tecnical.test.rest.util.GeneralRestUtil;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class ValidationEmployeeService implements IValidationEmployeeService {

    private final IGroovieScriptExecutorService iGroovieScriptExecutorService;

    @Qualifier("rest-script")
    private final ScriptValidationRepository scriptValidationRepository;

    private final ScriptValidationMapper scriptValidationMapper;

    @Override
    public ResponseGeneralDTO validationEmployee(EmployeeRequestDTO employeeRequest) {
        ResponseGeneralDTO responseGeneral = new ResponseGeneralDTO();
        responseGeneral.setStatus(HttpStatus.OK.value());
        if(employeeRequest == null){
            return responseGeneral;
        }
        List<ResponseValidationGroovieDTO> responseValidationGroovie = new ArrayList<>();
        Map<String, Object> valuesExtras = new HashMap<>();
        valuesExtras.put("generalUtilRest", GeneralRestUtil.class);
        valuesExtras.put("generalUtil", GeneralUtil.class);
        valuesExtras.put("listValidation", responseValidationGroovie);
        iGroovieScriptExecutorService.runScript(employeeRequest, valuesExtras, scriptValidationMapper.toListDto(scriptValidationRepository.searchActiveValidationsGroovie()));
        for(ResponseValidationGroovieDTO response : responseValidationGroovie){
            if(response.isError()){
                responseGeneral.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
                responseGeneral.setMessage(response.getMessage());
                break;
            }
        }
        return responseGeneral;
    }
}
