package co.parameta.technical.test.rest.util.mapper;

import co.parameta.technical.test.commons.pojo.*;
import co.parameta.technical.test.commons.util.helper.GeneralUtil;
import co.parameta.technical.test.rest.dto.EmployeeRequestDTO;
import co.parameta.technical.test.rest.util.helper.GeneralRestUtil;
import org.mapstruct.Mapper;

/**
 * Mapper responsible for transforming REST JSON DTOs into SOAP POJOs.
 * <p>
 * This mapper builds the SOAP request structure expected by the employee SOAP service,
 * including nested objects such as {@link TypeDocumentPojo}, {@link PositionPojo} and
 * {@link AdministratorUserPojo}.
 */
@Mapper(componentModel = "spring")
public interface JsonToPojoMapper {

    /**
     * Creates a {@link SaveEmployeeRequestPojo} used to invoke the SOAP service.
     * <p>
     * Dates are converted to {@link javax.xml.datatype.XMLGregorianCalendar} using
     * {@link GeneralRestUtil#fromString(String)} to support multiple formats.
     *
     * @param employeeRequest        REST request with employee data
     * @param administratorUserCode  administrator user code to include in the SOAP request
     * @return a populated {@link SaveEmployeeRequestPojo}, or {@code null} if input is null
     */
    default SaveEmployeeRequestPojo toSaveEmployeeRequest(
            EmployeeRequestDTO employeeRequest,
            String administratorUserCode
    ) {

        if (employeeRequest == null) {
            return null;
        }

        EmployeePojo employeePojo = new EmployeePojo();

        employeePojo.setNames(
                GeneralUtil.get(employeeRequest::getNames, null)
        );

        employeePojo.setLastNames(
                GeneralUtil.get(employeeRequest::getLastNames, null)
        );

        TypeDocumentPojo typeDocumentPojo = new TypeDocumentPojo();
        typeDocumentPojo.setCode(
                GeneralUtil.get(employeeRequest::getTypeDocument, null)
        );
        employeePojo.setTypeDocument(typeDocumentPojo);

        employeePojo.setDocumentNumber(
                GeneralUtil.get(employeeRequest::getDocumentNumber, null)
        );

        employeePojo.setDateOfBirth(
                GeneralUtil.get(
                        () -> GeneralRestUtil.fromString(employeeRequest.getDateOfBirth()),
                        null
                )
        );

        employeePojo.setDateAffiliationCompany(
                GeneralUtil.get(
                        () -> GeneralRestUtil.fromString(employeeRequest.getDateAffiliationCompany()),
                        null
                )
        );

        PositionPojo positionPojo = new PositionPojo();
        positionPojo.setCode(
                GeneralUtil.get(employeeRequest::getPosition, null)
        );
        employeePojo.setPosition(positionPojo);

        employeePojo.setSalary(
                GeneralUtil.get(employeeRequest::getSalary, null)
        );

        AdministratorUserPojo administratorUserPojo = new AdministratorUserPojo();
        administratorUserPojo.setCode(administratorUserCode);
        employeePojo.setAdministratorUser(administratorUserPojo);

        SaveEmployeeRequestPojo request = new SaveEmployeeRequestPojo();
        request.setEmployee(employeePojo);

        return request;
    }
}
