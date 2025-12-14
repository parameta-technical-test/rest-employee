package co.parameta.technical.test.rest.util.mapper;

import co.parameta.technical.test.commons.dto.EmployeeDTO;
import co.parameta.technical.test.commons.entity.EmployeeEntity;
import co.parameta.technical.test.commons.util.mapper.BaseMapper;
import co.parameta.technical.test.rest.dto.AllInformationEmployeeDTO;
import co.parameta.technical.test.rest.dto.ExtraInformationDTO;
import org.mapstruct.Mapper;

/**
 * MapStruct mapper for converting {@link EmployeeEntity} objects to {@link EmployeeDTO} and vice versa.
 * <p>
 * Also provides a helper method to build an {@link AllInformationEmployeeDTO} by enriching the base
 * employee data with calculated extra information and an optional PDF payload.
 */
@Mapper(componentModel = "spring")
public interface EmployeeMapper extends BaseMapper<EmployeeEntity, EmployeeDTO> {

    /**
     * Builds an {@link AllInformationEmployeeDTO} from a base {@link EmployeeDTO}, adding:
     * <ul>
     *   <li>Time linked to the company</li>
     *   <li>Current employee age</li>
     *   <li>Optional informative PDF (byte[])</li>
     * </ul>
     * <p>
     * Note: this method also clears sensitive fields from the administrator user
     * (e.g. code and password) before returning the response.
     *
     * @param employee         base employee data
     * @param timeAtCompany    calculated time the employee has been linked to the company
     * @param ageEmployee      calculated current age of the employee
     * @param informativePdf   optional PDF bytes (may be null)
     * @return fully populated {@link AllInformationEmployeeDTO}
     */
    default AllInformationEmployeeDTO employeeDTOToAllInformationEmployeeDTO(
            EmployeeDTO employee,
            ExtraInformationDTO timeAtCompany,
            ExtraInformationDTO ageEmployee,
            byte[] informativePdf
    ) {
        AllInformationEmployeeDTO allInformationEmployee = new AllInformationEmployeeDTO();

        allInformationEmployee.setId(employee.getId());
        allInformationEmployee.setNames(employee.getNames());
        allInformationEmployee.setLastNames(employee.getLastNames());
        allInformationEmployee.setTypeDocument(employee.getTypeDocument());
        allInformationEmployee.setDocumentNumber(employee.getDocumentNumber());
        allInformationEmployee.setDateOfBirth(employee.getDateOfBirth());
        allInformationEmployee.setDateAffiliationCompany(employee.getDateAffiliationCompany());
        allInformationEmployee.setPosition(employee.getPosition());
        allInformationEmployee.setSalary(employee.getSalary());

        if (employee.getAdministratorUser() != null) {
            employee.getAdministratorUser().setCode(null);
            employee.getAdministratorUser().setPasswordEncoder(null);
        }

        allInformationEmployee.setAdministratorUser(employee.getAdministratorUser());
        allInformationEmployee.setDateCreate(employee.getDateCreate());
        allInformationEmployee.setDateUpdate(employee.getDateUpdate());
        allInformationEmployee.setTimeLinkedToCompany(timeAtCompany);
        allInformationEmployee.setCurrentAgeEmployee(ageEmployee);
        allInformationEmployee.setInformativePdf(informativePdf);
        allInformationEmployee.setStorageLocationReport(employee.getStorageLocationReport());

        return allInformationEmployee;
    }
}
