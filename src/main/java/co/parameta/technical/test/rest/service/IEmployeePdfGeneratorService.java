package co.parameta.technical.test.rest.service;

import co.parameta.technical.test.rest.dto.EmployeeRequestDTO;

public interface IEmployeePdfGeneratorService {

    byte[] generateEmployeeReport(EmployeeRequestDTO employee);

}
