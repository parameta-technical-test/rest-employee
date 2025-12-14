package co.parameta.technical.test.rest.service;

import co.parameta.technical.test.rest.dto.EmployeeRequestDTO;

/**
 * Service interface responsible for generating employee PDF reports.
 * <p>
 * Implementations of this interface must create a PDF document containing
 * relevant employee information, which can later be stored or sent via email.
 * </p>
 */
public interface IEmployeePdfGeneratorService {

    /**
     * Generates a PDF report for the given employee.
     *
     * @param employee the employee information used to generate the report
     * @return a byte array representing the generated PDF file
     * @throws IllegalArgumentException if the provided employee is {@code null}
     */
    byte[] generateEmployeeReport(EmployeeRequestDTO employee, boolean isUpdate);

}
