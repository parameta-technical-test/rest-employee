package co.parameta.technical.test.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * Data Transfer Object that represents the employee request payload.
 * <p>
 * This DTO is used to receive employee information for validation,
 * registration, or update operations within the system.
 * </p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(
        name = "EmployeeRequest",
        description = "Request object containing employee information for validation and registration"
)
public class EmployeeRequestDTO {

    /**
     * Employee first names.
     */
    @Schema(
            description = "Employee first names",
            example = "Juan Pablo"
    )
    private String names;

    /**
     * Employee last names.
     */
    @Schema(
            description = "Employee last names",
            example = "Pérez Gómez"
    )
    private String lastNames;

    /**
     * Code representing the type of identification document.
     */
    @Schema(
            description = "Type of document code",
            example = "CC"
    )
    private String typeDocument;

    /**
     * Identification document number of the employee.
     */
    @Schema(
            description = "Employee document number",
            example = "1020304050"
    )
    private String documentNumber;

    /**
     * Employee date of birth.
     * <p>
     * Expected format: yyyy-MM-dd
     * </p>
     */
    @Schema(
            description = "Employee date of birth (yyyy-MM-dd)",
            example = "1995-08-21"
    )
    private String dateOfBirth;

    /**
     * Date when the employee was affiliated with the company.
     * <p>
     * Expected format: yyyy-MM-dd
     * </p>
     */
    @Schema(
            description = "Company affiliation date (yyyy-MM-dd)",
            example = "2020-01-15"
    )
    private String dateAffiliationCompany;

    /**
     * Code representing the employee's position within the company.
     */
    @Schema(
            description = "Employee position code",
            example = "DEV"
    )
    private String position;

    /**
     * Employee email address.
     * <p>
     * This field is optional and may be null.
     * </p>
     */
    @Schema(
            description = "Employee email address",
            example = "juan.perez@email.com",
            nullable = true
    )
    private String email;

    /**
     * Employee salary amount.
     */
    @Schema(
            description = "Employee salary",
            example = "3500000.00"
    )
    private String salary;
}
