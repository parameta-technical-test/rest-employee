package co.parameta.technical.test.rest.util.mapper;

import co.parameta.technical.test.commons.pojo.*;
import co.parameta.technical.test.commons.util.helper.GeneralUtil;
import co.parameta.technical.test.rest.dto.EmployeeRequestDTO;
import co.parameta.technical.test.rest.dto.ExtraInformationDTO;
import co.parameta.technical.test.rest.dto.ResponseEmployeeDTO;
import org.mapstruct.Mapper;

/**
 * Mapper responsible for converting SOAP response POJOs into REST JSON DTOs.
 * <p>
 * This mapper adapts the response obtained from the SOAP employee service
 * into a REST-friendly structure, combining data from the original request
 * and the SOAP response metadata.
 */
@Mapper(componentModel = "spring")
public interface PojoToJsonMapper {

    /**
     * Builds a {@link ResponseEmployeeDTO} from the SOAP response and
     * the original employee request.
     * <p>
     * The response includes calculated information such as time linked to the company
     * and current employee age when available.
     *
     * @param employeeResponse SOAP response containing employee processing result
     * @param employee         original REST employee request
     * @return a populated {@link ResponseEmployeeDTO}, or {@code null} if inputs are null
     */
    default ResponseEmployeeDTO toResponseEmployeeDto(
            EmployeeResponsePojo employeeResponse,
            EmployeeRequestDTO employee
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
        responseEmployee.setTypeDocument(
                GeneralUtil.get(employee::getTypeDocument, null)
        );

        responseEmployee.setDocumentNumber(
                GeneralUtil.get(employee::getDocumentNumber, null)
        );

        responseEmployee.setDateOfBirth(
                GeneralUtil.get(employee::getDateOfBirth, null)
        );

        responseEmployee.setDateAffiliationCompany(
                GeneralUtil.get(employee::getDateAffiliationCompany, null)
        );

        responseEmployee.setPosition(
                GeneralUtil.get(employee::getPosition, null)
        );

        responseEmployee.setSalary(
                GeneralUtil.get(employee::getSalary, null)
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

    /**
     * Converts a {@link ExtraInformationPojo} into its REST DTO representation.
     * <p>
     * Used to map calculated values such as years, months and days.
     *
     * @param extraInformationPojo source POJO with calculated values
     * @return mapped {@link ExtraInformationDTO}, or {@code null} if input is null
     */
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
