package co.parameta.tecnical.test.rest.controller;


import co.parameta.tecnical.test.commons.dto.ResponseGeneralDTO;
import co.parameta.tecnical.test.rest.dto.EmployeeRequestDTO;
import co.parameta.tecnical.test.rest.service.IValidationEmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;

@RestController
@RequestMapping("/employee")
@RequiredArgsConstructor
public class EmployeeController {

    private final IValidationEmployeeService iValidationEmployeeService;

    @GetMapping("/bridge")
    public ResponseEntity<ResponseGeneralDTO> guardarEmpleado(
            @Valid EmployeeRequestDTO employeeDTO
    )  {
        ResponseGeneralDTO respuestaGeneralDTO = iValidationEmployeeService.validationEmployee(employeeDTO);
       return ResponseEntity.status(respuestaGeneralDTO.getStatus()).body(respuestaGeneralDTO);
    }

}
