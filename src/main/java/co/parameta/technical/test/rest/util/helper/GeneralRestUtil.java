package co.parameta.technical.test.rest.util.helper;

import co.parameta.technical.test.rest.dto.ExtraInformationDTO;
import groovy.util.logging.Slf4j;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.*;

import static co.parameta.technical.test.rest.util.constant.Constants.*;

/**
 * Utility class containing common helper methods used in REST layer.
 * <p>
 * Provides null-safe validations, date parsing utilities and
 * helper methods for formatting and transformation.
 */
@lombok.extern.slf4j.Slf4j
@Slf4j
public final class GeneralRestUtil {

    private GeneralRestUtil() {
    }

    /**
     * Checks whether a value is null, blank or empty.
     * <p>
     * Supports {@link String}, {@link Collection} and {@link Map}.
     *
     * @param value the value to evaluate
     * @return {@code true} if the value is null, blank or empty
     */
    public static boolean isNullOrBlank(Object value) {
        if (value == null) {
            return true;
        }

        if (value instanceof String str) {
            return str.trim().isEmpty();
        }

        if (value instanceof Collection<?> col) {
            return col.isEmpty();
        }

        if (value instanceof Map<?, ?> map) {
            return map.isEmpty();
        }

        return false;
    }

    /**
     * Validates whether a string matches any supported date or datetime format.
     *
     * @param value the date string to validate
     * @return {@code true} if the value can be parsed using known formats
     */
    public static boolean isValidDateFormat(String value) {
        if (value == null) {
            return false;
        }

        String v = value.trim();
        if (v.isEmpty()) {
            return false;
        }

        v = v.replaceAll("\\s+", " ");

        for (DateTimeFormatter formatter : FORMATTERS) {
            if (tryParseSmart(v, formatter)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Attempts to parse a date string using multiple temporal types.
     *
     * @param value     date value
     * @param formatter formatter to apply
     * @return {@code true} if parsing succeeds
     */
    private static boolean tryParseSmart(String value, DateTimeFormatter formatter) {
        try {
            LocalDate.parse(value, formatter);
            return true;
        } catch (DateTimeParseException ignored) {
            log.info(ERROR_IGNORE);
        }

        try {
            LocalDateTime.parse(value, formatter);
            return true;
        } catch (DateTimeParseException ignored) {
            log.info(ERROR_IGNORE);
        }

        try {
            OffsetDateTime.parse(value, formatter);
            return true;
        } catch (DateTimeParseException ignored) {
            log.info(ERROR_IGNORE);
        }

        try {
            Instant.parse(value);
            return true;
        } catch (Exception ignored) {
            log.info(ERROR_IGNORE);
        }

        return false;
    }

    /**
     * Parses a string into a {@link Date} using multiple supported formats.
     *
     * @param value the date string
     * @return parsed {@link Date} or {@code null} if input is blank
     * @throws IllegalArgumentException if the value cannot be parsed
     */
    public static Date parseToDate(String value) {
        if (value == null) {
            return null;
        }

        String v = value.trim();
        if (v.isEmpty()) {
            return null;
        }

        v = v.replaceAll("\\s+", " ");

        try {
            return Date.from(Instant.parse(v));
        } catch (Exception ignored) {
            log.info(ERROR_IGNORE);
        }

        for (DateTimeFormatter formatter : FORMATTERS) {

            try {
                OffsetDateTime odt = OffsetDateTime.parse(v, formatter);
                return Date.from(odt.toInstant());
            } catch (DateTimeParseException ignored) {
                log.info(ERROR_IGNORE);
            }

            try {
                ZonedDateTime zdt = ZonedDateTime.parse(v, formatter);
                return Date.from(zdt.toInstant());
            } catch (DateTimeParseException ignored) {
                log.info(ERROR_IGNORE);
            }

            try {
                LocalDateTime ldt = LocalDateTime.parse(v, formatter);
                return Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
            } catch (DateTimeParseException ignored) {
                log.info(ERROR_IGNORE);
            }

            try {
                LocalDate ld = LocalDate.parse(v, formatter);
                return Date.from(ld.atStartOfDay(ZoneId.systemDefault()).toInstant());
            } catch (DateTimeParseException ignored) {
                log.info(ERROR_IGNORE);
            }
        }

        throw new IllegalArgumentException(String.format(INVALID_DATE_FORMAT, v));
    }

    /**
     * Converts a {@link Date} to a date-only String using the pattern:
     * <pre>
     * yyyy-MM-dd
     * </pre>
     *
     * @param date the date to format
     * @return formatted date string or {@code null} if date is null
     */
    public static String dateToDateString(Date date) {

        if (date == null) {
            return null;
        }

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("uuuu-MM-dd");

        return formatter.format(
                date.toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate()
        );
    }



    /**
     * Converts a date string into an {@link XMLGregorianCalendar}.
     *
     * @param value the date string
     * @return parsed {@link XMLGregorianCalendar}
     * @throws IllegalArgumentException if parsing fails
     */
    public static XMLGregorianCalendar fromString(String value) {
        if (value == null) return null;

        String v = value.trim();
        if (v.isEmpty()) return null;

        v = v.replaceAll("\\s+", " ");

        final DatatypeFactory df;
        try {
            df = DatatypeFactory.newInstance();
        } catch (DatatypeConfigurationException e) {
            throw new IllegalStateException(DATATYPE_FACTORY_INIT_ERROR, e);
        }

        try {
            Instant instant = Instant.parse(v);
            return df.newXMLGregorianCalendar(
                    GregorianCalendar.from(instant.atZone(ZoneId.systemDefault()))
            );
        } catch (Exception ignored) {
            log.info(ERROR_IGNORE);
        }

        for (DateTimeFormatter baseFormatter : FORMATTERS) {
            DateTimeFormatter formatter = baseFormatter.withResolverStyle(ResolverStyle.STRICT);

            try {
                OffsetDateTime odt = OffsetDateTime.parse(v, formatter);
                return df.newXMLGregorianCalendar(GregorianCalendar.from(odt.toZonedDateTime()));
            } catch (DateTimeParseException ignored) {
                log.info(ERROR_IGNORE);
            }

            try {
                ZonedDateTime zdt = ZonedDateTime.parse(v, formatter);
                return df.newXMLGregorianCalendar(GregorianCalendar.from(zdt));
            } catch (DateTimeParseException ignored) {
                log.info(ERROR_IGNORE);
            }

            try {
                LocalDateTime ldt = LocalDateTime.parse(v, formatter);
                return df.newXMLGregorianCalendar(
                        GregorianCalendar.from(ldt.atZone(ZoneId.systemDefault()))
                );
            } catch (DateTimeParseException ignored) {
                log.info(ERROR_IGNORE);
            }

            try {
                LocalDate ld = LocalDate.parse(v, formatter);
                return df.newXMLGregorianCalendar(
                        GregorianCalendar.from(ld.atStartOfDay(ZoneId.systemDefault()))
                );
            } catch (DateTimeParseException ignored) {
                log.info(ERROR_IGNORE);
            }
        }

        throw new IllegalArgumentException(String.format(INVALID_DATE_FORMAT, v));
    }


    /**
     * Returns a safe alphabetical prefix from a string.
     *
     * @param value  input value
     * @param length desired prefix length
     * @return uppercase alphabetical prefix padded with 'X'
     */
    public static String safePrefix(String value, int length) {
        if (value == null || value.isBlank()) {
            return "XX";
        }
        String clean = value.replaceAll("[^A-Za-z]", "").toUpperCase();
        return clean.length() >= length
                ? clean.substring(0, length)
                : clean + "X".repeat(length - clean.length());
    }

    /**
     * Returns a numeric prefix padded with zeros.
     *
     * @param value  input value
     * @param length desired prefix length
     * @return numeric prefix
     */
    public static String safeDigitsPrefix(String value, int length) {
        if (length <= 0) {
            return "";
        }

        if (value == null) {
            return "0".repeat(length);
        }

        String digits = value.replaceAll("\\D", "");

        if (digits.isEmpty()) {
            return "0".repeat(length);
        }

        return digits.length() >= length
                ? digits.substring(0, length)
                : "0".repeat(length - digits.length()) + digits;
    }


    /**
     * Returns an uppercase string or a default value if blank.
     *
     * @param value input value
     * @return uppercase value or {@code NA}
     */
    public static String safeUpper(String value) {
        return (value == null || value.isBlank())
                ? "NA"
                : value.toUpperCase();
    }

    /**
     * Converts a date difference map into an {@link ExtraInformationDTO}.
     *
     * @param data map containing years, months and days
     * @return populated {@link ExtraInformationDTO}
     */
    public static ExtraInformationDTO toExtraInformation(Map<String, Integer> data) {
        ExtraInformationDTO extra = new ExtraInformationDTO();
        extra.setYears(data.get(YEARS));
        extra.setMonths(data.get(MONTHS));
        extra.setDays(data.get(DAYS));
        return extra;
    }

    /**
     * Builds the full name of an employee by concatenating first names and last names.
     *
     * <p>
     * This method safely handles {@code null} or blank values and trims extra spaces.
     * </p>
     *
     * @param names the employee first names
     * @param lastNames the employee last names
     * @return the full name as a single string, or an empty string if both values are null or blank
     */
    public static String buildFullName(String names, String lastNames) {
        String first = names != null ? names.trim() : "";
        String last = lastNames != null ? lastNames.trim() : "";

        if (first.isEmpty() && last.isEmpty()) {
            return "";
        }

        if (first.isEmpty()) {
            return last;
        }

        if (last.isEmpty()) {
            return first;
        }

        return first + " " + last;
    }

    /**
     * Safely converts a {@link Double} to {@link BigDecimal}.
     *
     * @param value the Double value
     * @return {@link BigDecimal} or {@code null} if value is null
     */
    public static BigDecimal doubleToBigDecimal(Double value) {

        if (value == null) {
            return null;
        }

        return BigDecimal.valueOf(value);
    }


}
