package co.parameta.technical.test.rest.controller;


import co.parameta.technical.test.commons.dto.ResponseGeneralDTO;
import co.parameta.technical.test.rest.dto.EmployeeRequestDTO;
import co.parameta.technical.test.rest.service.IValidationEmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/employee")
@RequiredArgsConstructor
public class EmployeeController {

    private final IValidationEmployeeService iValidationEmployeeService;

    @GetMapping("/bridge")
    public ResponseEntity<ResponseGeneralDTO> employeeSave(
            @Valid EmployeeRequestDTO employeeDTO
    )  {
        ResponseGeneralDTO respuestaGeneralDTO = iValidationEmployeeService.validationEmployee(employeeDTO);
       return ResponseEntity.status(respuestaGeneralDTO.getStatus()).body(respuestaGeneralDTO);
    }

}
