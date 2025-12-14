package co.parameta.technical.test.rest.service.impl;

import co.parameta.technical.test.commons.dto.EmployeeDTO;
import co.parameta.technical.test.commons.dto.ResponseGeneralDTO;
import co.parameta.technical.test.commons.util.helper.GeneralUtil;
import co.parameta.technical.test.rest.dto.AllInformationEmployeeDTO;
import co.parameta.technical.test.rest.repository.EmployeeRepository;
import co.parameta.technical.test.rest.repository.SystemParameterRepository;
import co.parameta.technical.test.rest.service.IGetPdfS3Service;
import co.parameta.technical.test.rest.service.IViewAllUserInformationService;
import co.parameta.technical.test.rest.util.helper.GeneralRestUtil;
import co.parameta.technical.test.rest.util.mapper.EmployeeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Service responsible for retrieving all employee information.
 * <p>
 * It supports querying by employee id or by document (type + number).
 * Optionally, it can include the employee PDF report from S3 depending on
 * the system parameter {@code GET_PDF_EMPLOYEE}.
 */
@Service
@RequiredArgsConstructor
public class ViewAllUserInformationService implements IViewAllUserInformationService {

    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;
    private final IGetPdfS3Service iGetPdfS3Service;
    private final SystemParameterRepository systemParameterRepository;

    /**
     * Retrieves employee information and returns it in a unified response.
     * <p>
     * If {@code GET_PDF_EMPLOYEE == "1"}, the PDF report is fetched from S3 and attached
     * to the response data. If no search criteria is provided, the response data is null.
     *
     * @param idEmployee     employee id (optional)
     * @param typeDocumnet   document type (optional, requires numberDocument)
     * @param numberDocument document number (optional, requires typeDocumnet)
     * @return a {@link ResponseGeneralDTO} containing the employee data when found
     */
    @Override
    public ResponseGeneralDTO allInformationEmployee(
            Integer idEmployee,
            String typeDocumnet,
            String numberDocument
    ) {

        boolean hasId = idEmployee != null;
        boolean hasDocumentData = typeDocumnet != null && numberDocument != null;
        boolean viewPdf = "1".equals(systemParameterRepository.findByName("GET_PDF_EMPLOYEE").getContent());

        AllInformationEmployeeDTO allInformation = null;

        ResponseGeneralDTO responseGeneral = new ResponseGeneralDTO();
        responseGeneral.setMessage("The information was consulted correctly");

        if (hasId || hasDocumentData) {
            EmployeeDTO employeeInformation = employeeMapper.toDto(
                    employeeRepository.searchAllInformationEmployee(idEmployee, typeDocumnet, numberDocument)
            );

            allInformation = employeeMapper.employeeDTOToAllInformationEmployeeDTO(
                    employeeInformation,
                    GeneralRestUtil.toExtraInformation(
                            GeneralUtil.diff(employeeInformation.getDateAffiliationCompany(), new Date())
                    ),
                    GeneralRestUtil.toExtraInformation(
                            GeneralUtil.diff(employeeInformation.getDateOfBirth(), new Date())
                    ),
                    viewPdf ? iGetPdfS3Service.getPdf(employeeInformation.getStorageLocationReport()) : null
            );
        }

        responseGeneral.setData(allInformation);
        responseGeneral.setStatus(HttpStatus.OK.value());
        return responseGeneral;
    }
}
