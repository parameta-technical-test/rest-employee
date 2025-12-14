package co.parameta.technical.test.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Data Transfer Object that represents the response returned after
 * validating and processing an employee.
 * <p>
 * This DTO contains the employee's basic information along with
 * calculated values such as:
 * <ul>
 *     <li>Time linked to the company</li>
 *     <li>Current age of the employee</li>
 * </ul>
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseEmployeeDTO {

    /**
     * Employee first names.
     */
    private String names;

    /**
     * Employee last names.
     */
    private String lastNames;

    /**
     * Type of document code.
     */
    private String typeDocument;

    /**
     * Employee document number.
     */
    private String documentNumber;

    /**
     * Employee date of birth formatted as yyyy-MM-dd.
     */
    private String dateOfBirth;

    /**
     * Company affiliation date formatted as yyyy-MM-dd.
     */
    private String dateAffiliationCompany;

    /**
     * Employee position code.
     */
    private String position;

    /**
     * Employee salary.
     */
    private BigDecimal salary;

    /**
     * Calculated time linked to the company expressed in years,
     * months, and days.
     */
    private ExtraInformationDTO timeLinkedToCompany;

    /**
     * Calculated current age of the employee expressed in years,
     * months, and days.
     */
    private ExtraInformationDTO currentAgeEmployee;

}
