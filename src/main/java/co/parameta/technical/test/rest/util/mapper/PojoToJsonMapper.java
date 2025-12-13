package co.parameta.technical.test.rest.util.mapper;

import co.parameta.technical.test.commons.dto.AdministratorUserDTO;
import co.parameta.technical.test.commons.dto.EmployeeDTO;
import co.parameta.technical.test.commons.dto.PositionDTO;
import co.parameta.technical.test.commons.dto.TypeDocumentDTO;
import co.parameta.technical.test.commons.pojo.*;
import co.parameta.technical.test.rest.dto.ExtraInformationDTO;
import co.parameta.technical.test.rest.dto.ResponseEmployeeDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PojoToJsonMapper {

    default ResponseEmployeeDTO toResponseEmployeeDto(
            EmployeeResponsePojo employeeResponse,
            EmployeeDTO employee
    ) {
        if (employeeResponse == null) {
            return null;
        }
        AdditionalEmployeeInformationPojo additional = employeeResponse.getResponse().getAdditionalEmployeeInformation();
        ResponseEmployeeDTO responseEmployee = new ResponseEmployeeDTO();
        responseEmployee.setNames(employee.getNames());
        responseEmployee.setLastNames(employee.getLastNames());
        TypeDocumentDTO typeDocument = new TypeDocumentDTO();
        typeDocument.setCode(employee.getTypeDocument().getCode());
        typeDocument.setDescription(employee.getTypeDocument().getDescription());
        responseEmployee.setTypeDocument(typeDocument);
        responseEmployee.setDocumentNumber(employee.getDocumentNumber());
        responseEmployee.setDateOfBirth(employee.getDateOfBirth());
        responseEmployee.setDateAffiliationCompany(employee.getDateAffiliationCompany());
        PositionDTO position = new PositionDTO();
        position.setCode(employee.getPosition().getCode());
        position.setDescription(employee.getPosition().getDescription());
        responseEmployee.setPosition(position);
        responseEmployee.setSalary(employee.getSalary());
        responseEmployee.setDateCreate(employee.getDateCreate());
        responseEmployee.setDateUpdate(employee.getDateUpdate());
        responseEmployee.setTimeLinkedToCompany(toExtraInformationDto(additional.getTimeLinkedToCompany()));
        responseEmployee.setCurrentAgeEmployee(toExtraInformationDto(additional.getCurrentAgeEmployee()));
        return new ResponseEmployeeDTO();
    }

    default ExtraInformationDTO toExtraInformationDto(ExtraInformationPojo extraInformationPojo){
        ExtraInformationDTO extraInformation = new ExtraInformationDTO();
        extraInformation.setDays(extraInformationPojo.getDays());
        extraInformation.setMonths(extraInformationPojo.getMonths());
        extraInformation.setYears(extraInformationPojo.getYears());
        return extraInformation;
    }

}
