package co.parameta.technical.test.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(
        name = "EmployeeRequest",
        description = "Request object containing employee information for validation and registration"
)
public class EmployeeRequestDTO {

    @Schema(
            description = "Employee first names",
            example = "Juan Pablo"
    )
    private String names;

    @Schema(
            description = "Employee last names",
            example = "Pérez Gómez"
    )
    private String lastNames;

    @Schema(
            description = "Type of document code",
            example = "CC"
    )
    private String typeDocument;

    @Schema(
            description = "Employee document number",
            example = "1020304050"
    )
    private String documentNumber;

    @Schema(
            description = "Employee date of birth (yyyy-MM-dd)",
            example = "1995-08-21"
    )
    private String dateOfBirth;

    @Schema(
            description = "Company affiliation date (yyyy-MM-dd)",
            example = "2020-01-15"
    )
    private String dateAffiliationCompany;

    @Schema(
            description = "Employee position code",
            example = "DEV"
    )
    private String position;

    @Schema(
            description = "Employee email address",
            example = "juan.perez@email.com",
            nullable = true
    )
    private String email;

    @Schema(
            description = "Employee salary",
            example = "3500000.00"
    )
    private BigDecimal salary;
}
