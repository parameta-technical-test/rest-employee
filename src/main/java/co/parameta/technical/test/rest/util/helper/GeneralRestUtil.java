package co.parameta.technical.test.rest.util.helper;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.function.Supplier;

import static co.parameta.technical.test.rest.util.constant.Constants.*;

public final class GeneralRestUtil {

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

    private static boolean tryParseSmart(String value, DateTimeFormatter formatter) {
        try {
            LocalDate.parse(value, formatter);
            return true;
        } catch (DateTimeParseException ignored) {}

        try {
            LocalDateTime.parse(value, formatter);
            return true;
        } catch (DateTimeParseException ignored) {}

        try {
            OffsetDateTime.parse(value, formatter);
            return true;
        } catch (DateTimeParseException ignored) {}

        try {
            Instant.parse(value);
            return true;
        } catch (Exception ignored) {}

        return false;
    }

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
        } catch (Exception ignored) {}

        for (DateTimeFormatter formatter : FORMATTERS) {

            try {
                OffsetDateTime odt = OffsetDateTime.parse(v, formatter);
                return Date.from(odt.toInstant());
            } catch (DateTimeParseException ignored) {}

            try {
                ZonedDateTime zdt = ZonedDateTime.parse(v, formatter);
                return Date.from(zdt.toInstant());
            } catch (DateTimeParseException ignored) {}

            try {
                LocalDateTime ldt = LocalDateTime.parse(v, formatter);
                return Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
            } catch (DateTimeParseException ignored) {}

            try {
                LocalDate ld = LocalDate.parse(v, formatter);
                return Date.from(ld.atStartOfDay(ZoneId.systemDefault()).toInstant());
            } catch (DateTimeParseException ignored) {}
        }

        throw new IllegalArgumentException(String.format(INVALID_DATE_FORMAT, v));
    }


    private static boolean tryParse(String value, DateTimeFormatter formatter) {
        try {
            LocalDate.parse(value, formatter);
            return true;
        } catch (DateTimeParseException e) {
            try {
                LocalDateTime.parse(value, formatter);
                return true;
            } catch (DateTimeParseException ex) {
                try {
                    OffsetDateTime.parse(value, formatter);
                    return true;
                } catch (DateTimeParseException ignored) {
                    return false;
                }
            }
        }
    }

    public static XMLGregorianCalendar fromString(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        try {
            DatatypeFactory df = DatatypeFactory.newInstance();

            try {
                OffsetDateTime odt = OffsetDateTime.parse(value);
                return df.newXMLGregorianCalendar(GregorianCalendar.from(odt.toZonedDateTime()));
            } catch (DateTimeParseException ignored) {}

            try {
                LocalDateTime ldt = LocalDateTime.parse(value);
                ZonedDateTime zdt = ldt.atZone(ZoneOffset.UTC);
                return df.newXMLGregorianCalendar(GregorianCalendar.from(zdt));
            } catch (DateTimeParseException ignored) {}

            try {
                LocalDate ld = LocalDate.parse(value);
                ZonedDateTime zdt = ld.atStartOfDay(ZoneOffset.UTC);
                return df.newXMLGregorianCalendar(GregorianCalendar.from(zdt));
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException(
                        String.format(INVALID_DATE_VALUE, value),
                        e
                );
            }
        } catch (DatatypeConfigurationException e) {
            throw new IllegalStateException(DATATYPE_FACTORY_INIT_ERROR, e);
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    String.format(DATE_CONVERSION_ERROR, value),
                    e
            );
        }
    }

    public static String safePrefix(String value, int length) {
        if (value == null || value.isBlank()) {
            return "XX";
        }
        String clean = value.replaceAll("[^A-Za-z]", "").toUpperCase();
        return clean.length() >= length
                ? clean.substring(0, length)
                : String.format("%-" + length + "s", clean).replace(' ', 'X');
    }

    public static String safeDigitsPrefix(String value, int length) {
        if (value == null) {
            return "000";
        }
        String digits = value.replaceAll("\\D", "");
        return digits.length() >= length
                ? digits.substring(0, length)
                : String.format("%0" + length + "d", Integer.parseInt(digits.isEmpty() ? "0" : digits));
    }

    public static String safeUpper(String value) {
        return (value == null || value.isBlank())
                ? "NA"
                : value.toUpperCase();
    }

}
