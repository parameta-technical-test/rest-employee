package co.parameta.technical.test.rest.service.impl;

import co.parameta.technical.test.commons.dto.ScriptValidationDTO;
import co.parameta.technical.test.rest.service.IGroovieScriptExecutorService;
import co.parameta.technical.test.rest.util.constant.Constants;
import groovy.lang.Binding;
import groovy.lang.Closure;
import groovy.lang.GroovyShell;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Service implementation responsible for executing dynamic Groovy validation scripts.
 * <p>
 * This service allows business validation rules to be defined and executed dynamically
 * at runtime using Groovy scripts stored externally (e.g. database).
 * </p>
 *
 * <p>
 * Features provided:
 * <ul>
 *     <li>Execution of multiple Groovy scripts in sequence</li>
 *     <li>Shared execution context available to all scripts</li>
 *     <li>Injection of extra variables into the Groovy binding</li>
 *     <li>Controlled SQL execution through helper closures</li>
 *     <li>Centralized error handling and logging</li>
 * </ul>
 * </p>
 */
@Service
@Log4j2
@RequiredArgsConstructor
public class GroovieScriptExecutorService implements IGroovieScriptExecutorService {

    /**
     * JDBC template used to execute SQL queries from Groovy scripts.
     */
    private final JdbcTemplate jdbcTemplate;

    /**
     * Executes a list of Groovy validation scripts sequentially.
     * <p>
     * Each script is executed using the same execution context and optional
     * extra variables. If any script throws an exception, execution stops
     * immediately and {@code null} is returned.
     * </p>
     *
     * @param context           the main execution context object accessible as {@code context} inside scripts
     * @param extras            additional variables exposed to the scripts (optional)
     * @param scriptValidations list of Groovy validation scripts to execute
     * @return a comma-separated string containing all script results,
     *         or {@code null} if any script execution fails
     */
    @Override
    public String runScript(
            Object context,
            Map<String, Object> extras,
            List<ScriptValidationDTO> scriptValidations
    ) {

        List<String> messages = new ArrayList<>();

        for (ScriptValidationDTO script : scriptValidations) {
            try {

                log.info(Constants.LOG_EXECUTING_SCRIPT, script.getCode());

                Object result = executeScript(
                        script.getGroovieCode(),
                        context,
                        extras
                );

                String finalMessage = result != null ? result.toString() : null;

                log.info(Constants.LOG_FINAL_MESSAGE, finalMessage);

                messages.add(finalMessage);

            } catch (Exception e) {
                log.error(
                        Constants.LOG_SCRIPT_EXECUTION_ERROR,
                        script.getGroovieCode(),
                        e
                );
                return null;
            }
        }

        return String.join(",", messages);
    }

    /**
     * Executes a single Groovy script using a prepared binding.
     * <p>
     * The following variables are available inside the script:
     * <ul>
     *     <li>{@code context} - main execution context</li>
     *     <li>{@code jdbcTemplate} - Spring JDBC template</li>
     *     <li>{@code log} - logger instance</li>
     *     <li>{@code executeSql(sql, params...)} - executes a SQL query returning a list</li>
     *     <li>{@code executeSingleSql(sql, params...)} - executes a SQL query returning a single row</li>
     *     <li>Any variable provided in {@code extras}</li>
     * </ul>
     * </p>
     *
     * @param groovyScript the Groovy script code to execute
     * @param context      the execution context object
     * @param extras       additional variables injected into the script
     * @return the result of the script execution
     * @throws RuntimeException if script execution fails
     */
    private Object executeScript(
            String groovyScript,
            Object context,
            Map<String, Object> extras
    ) {
        try {
            Binding binding = new Binding();
            binding.setVariable("context", context);
            binding.setVariable("jdbcTemplate", jdbcTemplate);
            binding.setVariable("log", log);

            if (extras != null) {
                extras.forEach((key, value) -> {
                    if (key != null && !key.trim().isEmpty()) {
                        try {
                            binding.setVariable(key.trim(), value);
                            log.info(
                                    Constants.LOG_BINDING_EXTRA_VARIABLE,
                                    key.trim(),
                                    value
                            );
                        } catch (Exception ex) {
                            log.warn(
                                    Constants.LOG_BINDING_EXTRA_ERROR,
                                    key.trim(),
                                    ex
                            );
                        }
                    }
                });
            }

            Closure<List<Map<String, Object>>> executeSqlClosure =
                    new Closure<List<Map<String, Object>>>(this, this) {
                        public List<Map<String, Object>> doCall(String sql, Object... params) {
                            return executeSql(sql, params);
                        }
                    };

            Closure<Map<String, Object>> executeSingleSqlClosure =
                    new Closure<Map<String, Object>>(this, this) {
                        public Map<String, Object> doCall(String sql, Object... params) {
                            return executeSingleSql(sql, params);
                        }
                    };

            binding.setVariable("executeSql", executeSqlClosure);
            binding.setVariable("executeSingleSql", executeSingleSqlClosure);

            GroovyShell shell = new GroovyShell(binding);
            return shell.evaluate(groovyScript);

        } catch (Exception e) {
            log.error(Constants.LOG_SCRIPT_ERROR, e.getMessage(), e);
            throw new RuntimeException(
                    Constants.EXCEPTION_SCRIPT_EXECUTION + e.getMessage(),
                    e
            );
        }
    }

    /**
     * Executes a SQL query and returns a list of result rows.
     *
     * @param sql    the SQL query to execute
     * @param params optional query parameters
     * @return a list of result rows represented as maps
     * @throws RuntimeException if SQL execution fails
     */
    private List<Map<String, Object>> executeSql(String sql, Object... params) {
        try {
            log.info(Constants.LOG_EXECUTING_SQL, sql);
            return jdbcTemplate.queryForList(sql, params);
        } catch (Exception e) {
            log.error(Constants.LOG_SQL_ERROR, sql, e);
            throw new RuntimeException(
                    Constants.EXCEPTION_SQL_EXECUTION + e.getMessage(),
                    e
            );
        }
    }

    /**
     * Executes a SQL query and returns the first result row.
     *
     * @param sql    the SQL query to execute
     * @param params optional query parameters
     * @return the first result row, or {@code null} if no rows are returned
     * @throws RuntimeException if SQL execution fails
     */
    private Map<String, Object> executeSingleSql(String sql, Object... params) {
        try {
            log.info(Constants.LOG_EXECUTING_SINGLE_SQL, sql);
            List<Map<String, Object>> results =
                    jdbcTemplate.queryForList(sql, params);
            return results.isEmpty() ? null : results.get(0);
        } catch (Exception e) {
            log.error(Constants.LOG_SQL_ERROR, sql, e);
            throw new RuntimeException(
                    Constants.EXCEPTION_SQL_EXECUTION + e.getMessage(),
                    e
            );
        }
    }
}
