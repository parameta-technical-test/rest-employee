package co.parameta.technical.test.rest.service;

import co.parameta.technical.test.commons.dto.ResponseGeneralDTO;

/**
 * Service interface responsible for retrieving complete employee information.
 * <p>
 * Allows querying employee data using different identification criteria,
 * such as employee ID or document information, and optionally includes
 * additional resources like PDF reports.
 * </p>
 */
public interface IViewAllUserInformationService {

    /**
     * Retrieves full employee information based on the provided criteria.
     * <p>
     * The search can be performed using:
     * <ul>
     *     <li>Employee identifier</li>
     *     <li>Document type and document number</li>
     * </ul>
     * </p>
     *
     * @param idEmployee     the employee identifier (optional)
     * @param typeDocumnet   the employee document type (optional)
     * @param numberDocument the employee document number (optional)
     * @return a {@link ResponseGeneralDTO} containing the employee information,
     *         or an empty response if no criteria are provided
     */
    ResponseGeneralDTO allInformationEmployee(
            Integer idEmployee,
            String typeDocumnet,
            String numberDocument
    );

}
