package co.parameta.technical.test.rest.controller;

import co.parameta.technical.test.commons.dto.ResponseGeneralDTO;
import co.parameta.technical.test.rest.dto.EmployeeRequestDTO;
import co.parameta.technical.test.rest.service.IValidationEmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/employee")
@RequiredArgsConstructor
@Tag(
        name = "Employee",
        description = "Operations related to employee validation and registration"
)
public class EmployeeController {

    private final IValidationEmployeeService iValidationEmployeeService;

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

        ResponseGeneralDTO respuestaGeneralDTO =
                iValidationEmployeeService.validationEmployee(employeeDTO);

        return ResponseEntity
                .status(respuestaGeneralDTO.getStatus())
                .body(respuestaGeneralDTO);
    }
}
