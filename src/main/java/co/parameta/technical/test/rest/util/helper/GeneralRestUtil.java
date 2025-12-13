package co.parameta.technical.test.rest.util.helper;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

import static co.parameta.technical.test.rest.util.constant.Constants.*;

public class GeneralRestUtil {

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
        if (value == null || value.trim().isEmpty()) {
            return false;
        }

        for (DateTimeFormatter formatter : FORMATTERS) {
            if (tryParse(value, formatter)) {
                return true;
            }
        }
        return false;
    }

    public static Date parseToDate(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        for (DateTimeFormatter formatter : FORMATTERS) {
            try {
                LocalDate ld = LocalDate.parse(value, formatter);
                return Date.from(ld.atStartOfDay(ZoneId.systemDefault()).toInstant());
            } catch (DateTimeParseException ignored) {}

            try {
                LocalDateTime ldt = LocalDateTime.parse(value, formatter);
                return Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
            } catch (DateTimeParseException ignored) {}

            try {
                OffsetDateTime odt = OffsetDateTime.parse(value, formatter);
                return Date.from(odt.toInstant());
            } catch (DateTimeParseException ignored) {}
        }

        throw new IllegalArgumentException(
                String.format(INVALID_DATE_FORMAT, value)
        );
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
}
