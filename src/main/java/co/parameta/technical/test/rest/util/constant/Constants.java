package co.parameta.technical.test.rest.util.constant;

import com.lowagie.text.Font;

import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Application-wide constants used across REST services,
 * validations, logging, security and PDF generation.
 */
public final class Constants {

    private Constants() {
    }

    /**
     * Supported date and datetime formats for parsing input values.
     */
    public static final List<DateTimeFormatter> FORMATTERS = List.of(

            DateTimeFormatter.ofPattern("uuuu-MM-dd"),
            DateTimeFormatter.ofPattern("uuuu/MM/dd"),
            DateTimeFormatter.ofPattern("uuuu.MM.dd"),
            DateTimeFormatter.ofPattern("uuuuMMdd"),

            DateTimeFormatter.ofPattern("dd/MM/uuuu"),
            DateTimeFormatter.ofPattern("dd-MM-uuuu"),
            DateTimeFormatter.ofPattern("dd.MM.uuuu"),
            DateTimeFormatter.ofPattern("ddMMuuuu"),

            DateTimeFormatter.ofPattern("MM/dd/uuuu"),
            DateTimeFormatter.ofPattern("MM-dd-uuuu"),

            DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm"),
            DateTimeFormatter.ofPattern("uuuu/MM/dd HH:mm"),
            DateTimeFormatter.ofPattern("uuuu.MM.dd HH:mm"),
            DateTimeFormatter.ofPattern("uuuuMMdd HH:mm"),

            DateTimeFormatter.ofPattern("dd/MM/uuuu HH:mm"),
            DateTimeFormatter.ofPattern("dd-MM-uuuu HH:mm"),
            DateTimeFormatter.ofPattern("ddMMuuuu HH:mm"),

            DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss"),
            DateTimeFormatter.ofPattern("uuuu/MM/dd HH:mm:ss"),
            DateTimeFormatter.ofPattern("uuuu.MM.dd HH:mm:ss"),
            DateTimeFormatter.ofPattern("uuuuMMdd HH:mm:ss"),

            DateTimeFormatter.ofPattern("dd/MM/uuuu HH:mm:ss"),
            DateTimeFormatter.ofPattern("dd-MM-uuuu HH:mm:ss"),
            DateTimeFormatter.ofPattern("dd.MM.uuuu HH:mm:ss"),
            DateTimeFormatter.ofPattern("ddMMuuuu HH:mm:ss"),

            DateTimeFormatter.ofPattern("MM/dd/uuuu HH:mm:ss"),
            DateTimeFormatter.ofPattern("MM-dd-uuuu HH:mm:ss"),

            DateTimeFormatter.ISO_LOCAL_DATE,
            DateTimeFormatter.ISO_LOCAL_DATE_TIME,
            DateTimeFormatter.ISO_OFFSET_DATE_TIME,
            DateTimeFormatter.ISO_ZONED_DATE_TIME,
            DateTimeFormatter.ISO_INSTANT
    );
    /** Log message when the user not found */
    public static final String ERR_STUDENT_NOT_FOUND = "User not found.";


    /** Log message when a Groovy validation script starts execution. */
    public static final String LOG_EXECUTING_SCRIPT =
            "Executing Groovy script for validation code: {}";

    /** Log message showing the final message returned by a script. */
    public static final String LOG_FINAL_MESSAGE =
            "Final message after processing: {}";

    /** Log message when an extra variable is added to Groovy binding. */
    public static final String LOG_BINDING_EXTRA_VARIABLE =
            "Registering extra variable in binding: {} = {}";

    /** Log warning when an extra variable cannot be bound. */
    public static final String LOG_BINDING_EXTRA_ERROR =
            "Could not register extra variable '{}' in binding";

    /** Log message before executing an SQL query from Groovy. */
    public static final String LOG_EXECUTING_SQL =
            "Executing SQL query: {}";

    /** Log message before executing a single-result SQL query. */
    public static final String LOG_EXECUTING_SINGLE_SQL =
            "Executing single-result SQL query: {}";

    /** Log error when an SQL execution fails. */
    public static final String LOG_SQL_ERROR =
            "Error executing SQL query: {}";

    /** Log error when a Groovy script fails. */
    public static final String LOG_SCRIPT_ERROR =
            "Error executing Groovy script: {}";

    /** Log error when a validation script throws an exception. */
    public static final String LOG_SCRIPT_EXECUTION_ERROR =
            "Error executing script for validation: {}";

    /** Exception prefix for Groovy script execution errors. */
    public static final String EXCEPTION_SCRIPT_EXECUTION =
            "Error executing validation script: ";

    /** Exception prefix for SQL execution errors. */
    public static final String EXCEPTION_SQL_EXECUTION =
            "Error executing SQL query: ";

    /** Message returned on successful login. */
    public static final String MSG_LOGIN_SUCCESS = "Login successful.";

    /** Generic OK message. */
    public static final String MSG_OK = "OK";

    /** Error message when no JWT token is provided. */
    public static final String ERR_TOKEN_MISSING = "No token was provided.";

    /** Error message when JWT token is invalid. */
    public static final String ERR_TOKEN_INVALID = "Invalid token.";

    /** Error message when JWT token is expired or revoked. */
    public static final String ERR_TOKEN_REVOKED = "Expired or revoked token.";

    /** Error message when user is not found. */
    public static final String ERR_USER_NOT_FOUND = "User not found.";

    /** Error message for invalid Authorization header format. */
    public static final String ERR_AUTH_HEADER_FORMAT =
            "Invalid authorization format. Use 'Bearer <token>'.";

    /** Error message for failed authentication attempt. */
    public static final String ERR_AUTHENTICATION_FAILED = "Authentication failed.";

    /** Log message when JWT is expired. */
    public static final String LOG_JWT_EXPIRED = "Expired JWT: {}";

    /** Log message when JWT is invalid. */
    public static final String LOG_JWT_INVALID = "Invalid JWT: {}";

    /** JWT Authorization header prefix. */
    public static final String BEARER_PREFIX = "Bearer ";

    /** Error message for invalid date formats. */
    public static final String INVALID_DATE_FORMAT =
            "Invalid date format. Received value: %s";

    /** Error message when DatatypeFactory cannot be initialized. */
    public static final String DATATYPE_FACTORY_INIT_ERROR =
            "Could not initialize DatatypeFactory";

    /** Font used for PDF footer rendering. */
    public static final Font FOOTER_FONT =
            new Font(Font.HELVETICA, 9, Font.NORMAL, new Color(120, 120, 120));

    /** Label used for year values. */
    public static final String YEARS = "years";

    /** Label used for month values. */
    public static final String MONTHS = "months";

    /** Label used for day values. */
    public static final String DAYS = "days";
}
