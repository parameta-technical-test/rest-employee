package co.parameta.technical.test.rest.service;

import co.parameta.technical.test.commons.dto.ScriptValidationDTO;
import co.parameta.technical.test.rest.service.impl.GroovieScriptExecutorService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GroovieScriptExecutorServiceTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private GroovieScriptExecutorService service;

    @Test
    void runScriptSingleScriptReturnsMessage() {
        ScriptValidationDTO script = new ScriptValidationDTO();
        script.setCode("S1");
        script.setGroovieCode("return 'OK'");

        String result = service.runScript(new Object(), Map.of(), List.of(script));

        assertEquals("OK", result);
    }

    @Test
    void runScriptMultipleScriptsConcatenatesMessages() {
        ScriptValidationDTO s1 = new ScriptValidationDTO();
        s1.setCode("S1");
        s1.setGroovieCode("return 'A'");

        ScriptValidationDTO s2 = new ScriptValidationDTO();
        s2.setCode("S2");
        s2.setGroovieCode("return 'B'");

        String result = service.runScript(new Object(), null, List.of(s1, s2));

        assertEquals("A,B", result);
    }

    @Test
    void runScriptWhenScriptFailsReturnsNull() {
        ScriptValidationDTO s1 = new ScriptValidationDTO();
        s1.setCode("S1");
        s1.setGroovieCode("return 'A'");

        ScriptValidationDTO s2 = new ScriptValidationDTO();
        s2.setCode("S2");
        s2.setGroovieCode("throw new RuntimeException('boom')");

        String result = service.runScript(new Object(), null, List.of(s1, s2));

        assertNull(result);
    }

    @Test
    void runScriptExecuteSqlReturnsValueFromJdbcTemplate() {
        ScriptValidationDTO script = new ScriptValidationDTO();
        script.setCode("SQL1");
        script.setGroovieCode("""
                def rows = executeSql("select 1 as v")
                return rows[0].v.toString()
                """);

        Map<String, Object> row = new HashMap<>();
        row.put("v", 1);

        when(jdbcTemplate.queryForList(anyString(), any(Object[].class)))
                .thenReturn(List.of(row));

        String result = service.runScript(new Object(), null, List.of(script));

        assertEquals("1", result);
        verify(jdbcTemplate, times(1))
                .queryForList(eq("select 1 as v"), any(Object[].class));
    }

    @Test
    void runScriptExecuteSingleSqlReturnsFirstRow() {
        ScriptValidationDTO script = new ScriptValidationDTO();
        script.setCode("SQL2");
        script.setGroovieCode("""
                def row = executeSingleSql("select 2 as v")
                return row.v.toString()
                """);

        Map<String, Object> row = new HashMap<>();
        row.put("v", 2);

        when(jdbcTemplate.queryForList(anyString(), any(Object[].class)))
                .thenReturn(List.of(row));

        String result = service.runScript(new Object(), null, List.of(script));

        assertEquals("2", result);
        verify(jdbcTemplate, times(1))
                .queryForList(eq("select 2 as v"), any(Object[].class));
    }

    @Test
    void runScriptExtrasCanBeUsedInScript() {
        ScriptValidationDTO script = new ScriptValidationDTO();
        script.setCode("EX1");
        script.setGroovieCode("return extraValue.toString()");

        Map<String, Object> extras = new HashMap<>();
        extras.put("extraValue", 999);

        String result = service.runScript(new Object(), extras, List.of(script));

        assertEquals("999", result);
    }
}