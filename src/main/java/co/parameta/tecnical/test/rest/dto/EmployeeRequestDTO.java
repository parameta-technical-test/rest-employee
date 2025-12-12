package co.parameta.tecnical.test.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeRequestDTO {

    private String names;
    private String lastNames;
    private String typeDocument;
    private String documentNumber;
    private String dateOfBirth;
    private String dateAffiliationCompany;
    private String position;
    private BigDecimal salary;

}
