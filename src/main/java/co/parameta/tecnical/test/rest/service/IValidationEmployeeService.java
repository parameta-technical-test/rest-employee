package co.parameta.tecnical.test.rest.service;

import co.parameta.tecnical.test.commons.dto.ResponseGeneralDTO;
import co.parameta.tecnical.test.rest.dto.EmployeeRequestDTO;

public interface IValidationEmployeeService {

    ResponseGeneralDTO validationEmployee(EmployeeRequestDTO employeeRequest);


}
