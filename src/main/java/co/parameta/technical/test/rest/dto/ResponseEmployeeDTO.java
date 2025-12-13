package co.parameta.technical.test.rest.dto;

import co.parameta.technical.test.commons.dto.EmployeeDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseEmployeeDTO extends EmployeeDTO {

    private ExtraInformationDTO timeLinkedToCompany;

    private ExtraInformationDTO currentAgeEmployee;

}
