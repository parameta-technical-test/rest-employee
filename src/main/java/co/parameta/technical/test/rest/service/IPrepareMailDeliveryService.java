package co.parameta.technical.test.rest.service;

import co.parameta.technical.test.rest.dto.EmployeeRequestDTO;
import jakarta.mail.MessagingException;

public interface IPrepareMailDeliveryService {

    void prepareMailDelivery(EmployeeRequestDTO employeeRequest) throws MessagingException;

}
