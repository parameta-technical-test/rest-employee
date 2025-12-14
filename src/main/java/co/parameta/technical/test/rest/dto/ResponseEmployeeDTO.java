package co.parameta.technical.test.rest.dto;

import co.parameta.technical.test.commons.dto.EmployeeDTO;
import co.parameta.technical.test.commons.dto.PositionDTO;
import co.parameta.technical.test.commons.dto.TypeDocumentDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseEmployeeDTO  {

    private String names;
    private String lastNames;
    private String typeDocument;
    private String documentNumber;
    private String dateOfBirth;
    private String dateAffiliationCompany;
    private String position;
    private BigDecimal salary;

    private ExtraInformationDTO timeLinkedToCompany;

    private ExtraInformationDTO currentAgeEmployee;

}
