package co.parameta.technical.test.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object that represents the result of a Groovy validation script.
 * <p>
 * It is used to indicate whether a validation rule failed and to provide
 * a descriptive message explaining the validation result.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseValidationGroovieDTO {

    /**
     * Message describing the validation result or error.
     */
    private String message;

    /**
     * Flag that indicates whether the validation resulted in an error.
     * <ul>
     *     <li>{@code true} if the validation failed</li>
     *     <li>{@code false} if the validation passed</li>
     * </ul>
     */
    private boolean error;

}
