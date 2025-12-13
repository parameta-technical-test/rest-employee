package co.parameta.technical.test.rest.util.mapper;

import co.parameta.technical.test.commons.dto.EmployeeDTO;
import co.parameta.technical.test.commons.dto.PositionDTO;
import co.parameta.technical.test.commons.dto.TypeDocumentDTO;
import co.parameta.technical.test.commons.pojo.*;
import co.parameta.technical.test.commons.util.helper.GeneralUtil;
import co.parameta.technical.test.rest.dto.ExtraInformationDTO;
import co.parameta.technical.test.rest.dto.ResponseEmployeeDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PojoToJsonMapper {

    default ResponseEmployeeDTO toResponseEmployeeDto(
            EmployeeResponsePojo employeeResponse,
            EmployeeDTO employee
    ) {

        if (employeeResponse == null || employee == null) {
            return null;
        }

        AdditionalEmployeeInformationPojo additional =
                GeneralUtil.get(
                        () -> employeeResponse.getResponse().getAdditionalEmployeeInformation(),
                        null
                );

        ResponseEmployeeDTO responseEmployee = new ResponseEmployeeDTO();

        responseEmployee.setNames(
                GeneralUtil.get(employee::getNames, null)
        );

        responseEmployee.setLastNames(
                GeneralUtil.get(employee::getLastNames, null)
        );
        TypeDocumentDTO typeDocument = new TypeDocumentDTO();
        typeDocument.setCode(
                GeneralUtil.get(() -> employee.getTypeDocument().getCode(), null)
        );
        typeDocument.setDescription(
                GeneralUtil.get(() -> employee.getTypeDocument().getDescription(), null)
        );
        responseEmployee.setTypeDocument(typeDocument);

        responseEmployee.setDocumentNumber(
                GeneralUtil.get(employee::getDocumentNumber, null)
        );

        responseEmployee.setDateOfBirth(
                GeneralUtil.get(employee::getDateOfBirth, null)
        );

        responseEmployee.setDateAffiliationCompany(
                GeneralUtil.get(employee::getDateAffiliationCompany, null)
        );

        PositionDTO position = new PositionDTO();
        position.setCode(
                GeneralUtil.get(() -> employee.getPosition().getCode(), null)
        );
        position.setDescription(
                GeneralUtil.get(() -> employee.getPosition().getDescription(), null)
        );
        responseEmployee.setPosition(position);

        responseEmployee.setSalary(
                GeneralUtil.get(employee::getSalary, null)
        );

        responseEmployee.setDateCreate(
                GeneralUtil.get(employee::getDateCreate, null)
        );

        responseEmployee.setDateUpdate(
                GeneralUtil.get(employee::getDateUpdate, null)
        );

        responseEmployee.setTimeLinkedToCompany(
                toExtraInformationDto(
                        GeneralUtil.get(
                                additional::getTimeLinkedToCompany,
                                null
                        )
                )
        );

        responseEmployee.setCurrentAgeEmployee(
                toExtraInformationDto(
                        GeneralUtil.get(
                                additional::getCurrentAgeEmployee,
                                null
                        )
                )
        );

        return responseEmployee;
    }

    default ExtraInformationDTO toExtraInformationDto(ExtraInformationPojo extraInformationPojo) {

        if (extraInformationPojo == null) {
            return null;
        }

        ExtraInformationDTO dto = new ExtraInformationDTO();
        dto.setDays(extraInformationPojo.getDays());
        dto.setMonths(extraInformationPojo.getMonths());
        dto.setYears(extraInformationPojo.getYears());
        return dto;
    }
}
