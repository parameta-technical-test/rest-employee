package co.parameta.tecnical.test.rest.util;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Collection;
import java.util.Map;

import static co.parameta.tecnical.test.rest.util.Constants.FORMATTERS;

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



}
