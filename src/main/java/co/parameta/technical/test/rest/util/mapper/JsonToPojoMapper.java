package co.parameta.technical.test.rest.util.mapper;

import co.parameta.technical.test.commons.pojo.*;
import co.parameta.technical.test.rest.dto.EmployeeRequestDTO;
import co.parameta.technical.test.rest.util.helper.GeneralRestUtil;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface JsonToPojoMapper {

    default SaveEmployeeRequestPojo toSaveEmployeeRequest(
            EmployeeRequestDTO employeeRequest,
            String administratorUserCode
    ) {

        if (employeeRequest == null) {
            return null;
        }
        EmployeePojo employeePojo = new EmployeePojo();
        employeePojo.setNames(employeeRequest.getNames());
        employeePojo.setLastNames(employeeRequest.getLastNames());
        TypeDocumentPojo typeDocumentPojo = new TypeDocumentPojo();
        typeDocumentPojo.setCode(employeeRequest.getTypeDocument());
        employeePojo.setTypeDocument(typeDocumentPojo);
        employeePojo.setDocumentNumber(employeeRequest.getDocumentNumber());
        employeePojo.setDateOfBirth(
                GeneralRestUtil.fromString(employeeRequest.getDateOfBirth())
        );
        employeePojo.setDateAffiliationCompany(
                GeneralRestUtil.fromString(employeeRequest.getDateAffiliationCompany())
        );
        PositionPojo positionPojo = new PositionPojo();
        positionPojo.setCode(employeeRequest.getPosition());
        employeePojo.setPosition(positionPojo);
        employeePojo.setSalary(employeeRequest.getSalary());
        AdministratorUserPojo administratorUserPojo = new AdministratorUserPojo();
        administratorUserPojo.setCode(administratorUserCode);
        employeePojo.setAdministratorUser(administratorUserPojo);
        SaveEmployeeRequestPojo request = new SaveEmployeeRequestPojo();
        request.setEmployee(employeePojo);
        return request;
    }

}
