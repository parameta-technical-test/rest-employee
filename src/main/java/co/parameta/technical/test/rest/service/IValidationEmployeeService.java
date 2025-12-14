package co.parameta.technical.test.rest.service;

import co.parameta.technical.test.commons.dto.ResponseGeneralDTO;
import co.parameta.technical.test.rest.dto.EmployeeRequestDTO;
import jakarta.mail.MessagingException;

/**
 * Service interface responsible for validating and processing employee information.
 * <p>
 * This service coordinates:
 * <ul>
 *     <li>Execution of dynamic Groovy validation scripts</li>
 *     <li>Interaction with external SOAP services</li>
 *     <li>Employee registration or update logic</li>
 *     <li>Optional email notification with PDF report</li>
 * </ul>
 * </p>
 */
public interface IValidationEmployeeService {

    /**
     * Validates and processes an employee request.
     * <p>
     * The validation flow includes business rule evaluation, persistence,
     * and optional notification depending on the provided data and system configuration.
     * </p>
     *
     * @param employeeRequest the employee information to validate and process
     * @return a {@link ResponseGeneralDTO} containing the result of the operation
     * @throws MessagingException if an error occurs while sending notification emails
     */
    ResponseGeneralDTO validationEmployee(EmployeeRequestDTO employeeRequest) throws MessagingException;

}
