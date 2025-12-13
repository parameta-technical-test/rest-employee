package co.parameta.technical.test.rest.service;


import co.parameta.technical.test.commons.dto.ScriptValidationDTO;

import java.util.List;
import java.util.Map;

public interface IGroovieScriptExecutorService {

    String runScript(Object contexto, Map<String, Object> extras,
                     List<ScriptValidationDTO> scriptsValidations);


}
