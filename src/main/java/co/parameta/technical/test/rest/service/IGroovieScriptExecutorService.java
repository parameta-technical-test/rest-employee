package co.parameta.technical.test.rest.service;

import co.parameta.technical.test.commons.dto.ScriptValidationDTO;

import java.util.List;
import java.util.Map;

/**
 * Service interface responsible for executing Groovy validation scripts.
 * <p>
 * This service allows dynamic execution of validation rules written in Groovy,
 * using a shared execution context and optional extra variables.
 * </p>
 */
public interface IGroovieScriptExecutorService {

    /**
     * Executes one or more Groovy validation scripts.
     * <p>
     * All scripts are executed sequentially using the same context.
     * If any script fails, the execution may stop depending on the implementation.
     * </p>
     *
     * @param contexto           the main execution context object accessible from the scripts
     * @param extras             additional variables available to the scripts
     *                           (e.g. lists, counters, or helper objects)
     * @param scriptsValidations list of Groovy validation scripts to execute
     * @return a concatenated result message from the executed scripts,
     *         or {@code null} if a script execution fails
     */
    String runScript(
            Object contexto,
            Map<String, Object> extras,
            List<ScriptValidationDTO> scriptsValidations
    );

}
