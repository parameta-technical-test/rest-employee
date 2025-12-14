package co.parameta.technical.test.rest.controller;

import co.parameta.technical.test.commons.dto.ResponseGeneralDTO;
import co.parameta.technical.test.rest.dto.EmployeeRequestDTO;
import co.parameta.technical.test.rest.service.IValidationEmployeeService;
import co.parameta.technical.test.rest.service.IViewAllUserInformationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller that exposes employee-related operations.
 * <p>
 * This controller provides endpoints for employee validation,
 * registration and information retrieval.
 * </p>
 */
@RestController
@RequestMapping("/employee")
@RequiredArgsConstructor
@Tag(
        name = "Employee",
        description = "Operations related to employee validation, registration and information retrieval"
)
public class EmployeeController {

    private final IValidationEmployeeService validationEmployeeService;
    private final IViewAllUserInformationService viewAllUserInformationService;

    /**
     * Validates and registers an employee.
     * <p>
     * This endpoint validates the employee information using business rules
     * and Groovy validations. If the validation is successful, the employee
     * is registered or updated accordingly.
     * </p>
     *
     * @param employeeDTO employee data used for validation and registration
     * @return response with the validation and registration result
     * @throws MessagingException if an error occurs while sending notification emails
     */
    @Operation(
            summary = "Validate and register an employee",
            description = "Validates employee information and registers or updates the employee if applicable."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Employee processed successfully",
            content = @Content(schema = @Schema(implementation = ResponseGeneralDTO.class))
    )
    @ApiResponse(
            responseCode = "400",
            description = "Invalid request data",
            content = @Content
    )
    @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content
    )
    @GetMapping("/bridge")
    public ResponseEntity<ResponseGeneralDTO> employeeSave(
            @Parameter(
                    description = "Employee data used for validation and registration",
                    required = true
            )
            @Valid EmployeeRequestDTO employeeDTO
    ) throws MessagingException {

        ResponseGeneralDTO response =
                validationEmployeeService.validationEmployee(employeeDTO);

        return ResponseEntity
                .status(response.getStatus())
                .body(response);
    }

    /**
     * Retrieves all available information for an employee.
     * <p>
     * The employee can be searched either by:
     * </p>
     * <ul>
     *     <li>Employee ID</li>
     *     <li>Document type and document number</li>
     * </ul>
     *
     * <p>
     * If no valid search criteria is provided, the response will contain
     * a successful status with {@code null} data.
     * </p>
     *
     * @param typeDocument employee document type (e.g. CC, TI)
     * @param numberDocument employee document number
     * @param idEmployee employee unique identifier
     * @return response containing the employee information
     */
    @Operation(
            summary = "Get all employee information",
            description = "Retrieves all available information of an employee using either the employee ID or the document data."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Employee information retrieved successfully",
            content = @Content(schema = @Schema(implementation = ResponseGeneralDTO.class))
    )
    @ApiResponse(
            responseCode = "400",
            description = "Invalid query parameters",
            content = @Content
    )
    @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content
    )
    @GetMapping("/all-user-information")
    public ResponseEntity<ResponseGeneralDTO> allUserInformation(
            @Parameter(
                    description = "Employee document type (optional if idEmployee is provided)",
                    example = "CC"
            )
            @Param("typeDocument") String typeDocument,

            @Parameter(
                    description = "Employee document number (optional if idEmployee is provided)",
                    example = "123456789"
            )
            @Param("numberDocument") String numberDocument,

            @Parameter(
                    description = "Employee unique identifier (optional if document data is provided)",
                    example = "10"
            )
            @Param("idEmployee") Integer idEmployee
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        viewAllUserInformationService
                                .allInformationEmployee(idEmployee, typeDocument, numberDocument)
                );
    }
}
