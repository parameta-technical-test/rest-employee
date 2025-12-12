package co.parameta.tecnical.test.rest.util;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class Constants {

    private Constants() {
    }

    public static final List<DateTimeFormatter> FORMATTERS = List.of(
            DateTimeFormatter.ofPattern("yyyy-MM-dd"),
            DateTimeFormatter.ofPattern("dd/MM/yyyy"),
            DateTimeFormatter.ofPattern("MM/dd/yyyy"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS"),
            DateTimeFormatter.ISO_LOCAL_DATE,
            DateTimeFormatter.ISO_LOCAL_DATE_TIME,
            DateTimeFormatter.ISO_OFFSET_DATE_TIME
    );

    public static final String LOG_EXECUTING_SCRIPT =
            "Executing Groovy script for validation code: {}";

    public static final String LOG_FINAL_MESSAGE =
            "Final message after processing: {}";

    public static final String LOG_BINDING_EXTRA_VARIABLE =
            "Registering extra variable in binding: {} = {}";

    public static final String LOG_BINDING_EXTRA_ERROR =
            "Could not register extra variable '{}' in binding";

    public static final String LOG_EXECUTING_SQL =
            "Executing SQL query: {}";

    public static final String LOG_EXECUTING_SINGLE_SQL =
            "Executing single-result SQL query: {}";

    public static final String LOG_SQL_ERROR =
            "Error executing SQL query: {}";

    public static final String LOG_SCRIPT_ERROR =
            "Error executing Groovy script: {}";

    public static final String LOG_SCRIPT_EXECUTION_ERROR =
            "Error executing script for validation: {}";

    public static final String EXCEPTION_SCRIPT_EXECUTION =
            "Error executing validation script: ";

    public static final String EXCEPTION_SQL_EXECUTION =
            "Error executing SQL query: ";

    public static final String MSG_LOGIN_SUCCESS = "Login successful.";
    public static final String MSG_OK = "OK";

    public static final String ERR_TOKEN_MISSING = "No token was provided.";
    public static final String ERR_TOKEN_INVALID = "Invalid token.";
    public static final String ERR_TOKEN_REVOKED = "Expired or revoked token.";
    public static final String ERR_USER_NOT_FOUND = "User not found.";
    public static final String ERR_AUTH_HEADER_FORMAT =
            "Invalid authorization format. Use 'Bearer <token>'.";

    public static final String ERR_AUTHENTICATION_FAILED = "Authentication failed.";
    public static final String ERR_STUDENT_NOT_FOUND = "User not found."; // keep key name if you already use it

    public static final String LOG_JWT_EXPIRED = "Expired JWT: {}";
    public static final String LOG_JWT_INVALID = "Invalid JWT: {}";

    public static final String BEARER_PREFIX = "Bearer ";

}
