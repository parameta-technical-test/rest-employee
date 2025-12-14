package co.parameta.technical.test.rest.dto;

import co.parameta.technical.test.commons.dto.EmployeeDTO;
import lombok.Data;

/**
 * Data Transfer Object that represents the complete information of an employee.
 * <p>
 * This DTO extends {@link EmployeeDTO} and adds calculated and additional
 * information derived from the employee data.
 * </p>
 *
 * <ul>
 *     <li>Time linked to the company</li>
 *     <li>Current age of the employee</li>
 *     <li>Informative PDF associated with the employee</li>
 * </ul>
 */
@Data
public class AllInformationEmployeeDTO extends EmployeeDTO {

    /**
     * Calculated information representing the time the employee
     * has been linked to the company.
     */
    private ExtraInformationDTO timeLinkedToCompany;

    /**
     * Calculated information representing the current age of the employee.
     */
    private ExtraInformationDTO currentAgeEmployee;

    /**
     * Informative PDF document associated with the employee.
     * <p>
     * This field contains the PDF content as a byte array.
     * </p>
     */
    private byte[] informativePdf;

}
