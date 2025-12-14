package co.parameta.technical.test.rest.dto;

import co.parameta.technical.test.commons.dto.PositionDTO;
import co.parameta.technical.test.commons.dto.TypeDocumentDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

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
    private TypeDocumentDTO typeDocument;

    /**
     * Employee document number.
     */
    private String documentNumber;

    /**
     * Employee date of birth formatted as yyyy-MM-dd.
     */
    private Date dateOfBirth;

    /**
     * Company affiliation date formatted as yyyy-MM-dd.
     */
    private Date dateAffiliationCompany;

    /**
     * Employee position code.
     */
    private PositionDTO position;

    /**
     * Employee salary.
     */
    private Double salary;

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
