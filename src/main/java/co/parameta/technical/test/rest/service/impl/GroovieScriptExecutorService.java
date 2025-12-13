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

@Service
@Log4j2
@RequiredArgsConstructor
public class GroovieScriptExecutorService implements IGroovieScriptExecutorService {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public String runScript(Object context,
                            Map<String, Object> extras,
                            List<ScriptValidationDTO> scriptValidations) {

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

    private Object executeScript(String groovyScript,
                                 Object context,
                                 Map<String, Object> extras) {
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

    private Map<String, Object> executeSingleSql(String sql, Object... params) {
        try {
            log.info(Constants.LOG_EXECUTING_SINGLE_SQL, sql);
            List<Map<String, Object>> results = jdbcTemplate.queryForList(sql, params);
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
