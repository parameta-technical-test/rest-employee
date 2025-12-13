package co.parameta.technical.test.rest.service;

import co.parameta.technical.test.commons.dto.ResponseGeneralDTO;
import co.parameta.technical.test.rest.dto.EmployeeRequestDTO;

public interface IValidationEmployeeService {

    ResponseGeneralDTO validationEmployee(EmployeeRequestDTO employeeRequest);


}
