package co.parameta.technical.test.rest.service.impl;

import co.parameta.technical.test.commons.dto.SystemParameterDTO;
import co.parameta.technical.test.commons.util.mapper.ScriptValidationMapper;
import co.parameta.technical.test.commons.util.mapper.SystemParameterMapper;
import co.parameta.technical.test.rest.dto.EmployeeRequestDTO;
import co.parameta.technical.test.rest.repository.ScriptValidationRepository;
import co.parameta.technical.test.rest.repository.SystemParameterRepository;
import co.parameta.technical.test.rest.service.*;
import co.parameta.technical.test.rest.util.helper.GeneralRestUtil;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static co.parameta.technical.test.rest.util.helper.GeneralRestUtil.*;

/**
 * Service implementation responsible for preparing and sending employee notification emails.
 * <p>
 * This service orchestrates the email delivery workflow by:
 * <ul>
 *     <li>Loading email configuration parameters from the database</li>
 *     <li>Optionally generating a PDF report for the employee</li>
 *     <li>Optionally uploading the generated PDF to Amazon S3</li>
 *     <li>Sending the email with optional CC/BCC recipients and attachment</li>
 * </ul>
 * </p>
 *
 * <p>
 * The email behavior is driven by system parameters, such as:
 * <ul>
 *     <li>EMAIL_SUBJECT</li>
 *     <li>EMAIL_CONTENT</li>
 *     <li>EMAIL_COPY</li>
 *     <li>EMAIL_SEND_ATTACHMENT</li>
 *     <li>SEND_EMAIL_WITH_COPY</li>
 *     <li>SEND_EMAIL_WITH_BLIND_COPY</li>
 *     <li>BLIND_COPY_EMAILS</li>
 * </ul>
 * </p>
 *
 */
@Service
@RequiredArgsConstructor
public class PrepareMailDeliveryService implements IPrepareMailDeliveryService {

    /**
     * Mapper used to convert {@code SystemParameterEntity} objects into {@link SystemParameterDTO}.
     */
    private final SystemParameterMapper systemParameterMapper;

    /**
     * Service used to generate an employee PDF report.
     */
    private final IEmployeePdfGeneratorService iEmployeePdfGeneratorService;

    /**
     * Service used to send email messages.
     */
    private final IMailDeliveryService iMailDeliveryService;

    /**
     * Repository used to retrieve system parameters for email configuration.
     */
    private final SystemParameterRepository systemParameterRepository;

    /**
     * Service used to upload generated PDF reports to Amazon S3.
     */
    private final IS3PdfStorageService is3PdfStorageService;

    private final IGroovieScriptExecutorService iGroovieScriptExecutorService;

    private final ScriptValidationRepository scriptValidationRepository;

    private final ScriptValidationMapper scriptValidationMapper;

    /**
     * Prepares and sends the notification email for an employee.
     * <p>
     * This method loads email parameters, optionally generates and uploads a PDF report,
     * and sends the email using the configured recipients.
     * </p>
     *
     * <p>
     * Attachment behavior:
     * <ul>
     *     <li>If {@code EMAIL_SEND_ATTACHMENT == "1"}, a PDF report is generated and uploaded to S3</li>
     *     <li>If {@code EMAIL_SEND_ATTACHMENT != "1"}, no report is generated or uploaded</li>
     * </ul>
     * </p>
     *
     * <p>
     * CC/BCC behavior:
     * <ul>
     *     <li>CC is included only if {@code SEND_EMAIL_WITH_COPY == "1"}</li>
     *     <li>BCC is included only if {@code SEND_EMAIL_WITH_BLIND_COPY == "1"}</li>
     * </ul>
     * </p>
     *
     * @param employeeRequest the employee information used for report generation and email delivery
     * @throws MessagingException if an error occurs while building or sending the email message
     */
    @Override
    @Async
    public void prepareMailDelivery(EmployeeRequestDTO employeeRequest, boolean isUpdate) throws MessagingException {
        Map<String, String> parameters;
        boolean isUpdateWithParam = systemParameterMapper.toDto(systemParameterRepository.findByName("UPDATE_INFORMATION")).getContent().equals("1") && isUpdate;
        boolean isSendAttachment;
        String subject;
        String content;
        List<String> emailCopy;
        List<String> blindCopyEmails;
        Map<String, Object> extraValues = new HashMap<>();
        extraValues.put("generalUtilRest", GeneralRestUtil.class);
        if(isUpdateWithParam){
            parameters =
                    systemParameterMapper.toListDto(systemParameterRepository.searchAllParameters(
                                    List.of(
                                            "EMAIL_SUBJECT_UPDATE",
                                            "EMAIL_CONTENT_UPDATE",
                                            "EMAIL_COPY_UPDATE",
                                            "EMAIL_SEND_ATTACHMENT_UPDATE",
                                            "SEND_EMAIL_WITH_COPY_UPDATE",
                                            "SEND_EMAIL_WITH_BLIND_COPY_UPDATE",
                                            "BLIND_COPY_EMAILS_UPDATE"
                                    )
                            )).stream()
                            .collect(Collectors.toMap(
                                    SystemParameterDTO::getName,
                                    SystemParameterDTO::getContent
                            ));
            isSendAttachment =parameters.get("EMAIL_SEND_ATTACHMENT_UPDATE").equals("1");
            subject = parameters.get("EMAIL_SUBJECT_UPDATE");
            extraValues.put("contentEmail", parameters.get("EMAIL_CONTENT_UPDATE"));
            content = iGroovieScriptExecutorService.runScript(employeeRequest,extraValues, List.of(scriptValidationMapper.toDto(scriptValidationRepository.findByCode("CAST_CONTENT_EMAIL_UPDATE"))));
            emailCopy = emailsToSend(parameters.get("EMAIL_COPY_UPDATE"), parameters.get("SEND_EMAIL_WITH_COPY_UPDATE"));
            blindCopyEmails = emailsToSend(parameters.get("BLIND_COPY_EMAILS_UPDATE"), parameters.get("SEND_EMAIL_WITH_BLIND_COPY_UPDATE"));
        }else{
            parameters =
                    systemParameterMapper.toListDto(systemParameterRepository.searchAllParameters(
                                    List.of(
                                            "EMAIL_SUBJECT",
                                            "EMAIL_CONTENT",
                                            "EMAIL_COPY",
                                            "EMAIL_SEND_ATTACHMENT",
                                            "SEND_EMAIL_WITH_COPY",
                                            "SEND_EMAIL_WITH_BLIND_COPY",
                                            "BLIND_COPY_EMAILS"
                                    )
                            )).stream()
                            .collect(Collectors.toMap(
                                    SystemParameterDTO::getName,
                                    SystemParameterDTO::getContent
                            ));
            isSendAttachment =parameters.get("EMAIL_SEND_ATTACHMENT").equals("1");
            subject = parameters.get("EMAIL_SUBJECT");
            extraValues.put("contentEmail", parameters.get("EMAIL_CONTENT"));
            content = iGroovieScriptExecutorService.runScript(employeeRequest,extraValues, List.of(scriptValidationMapper.toDto(scriptValidationRepository.findByCode("CAST_CONTENT_EMAIL"))));
            emailCopy = emailsToSend(parameters.get("EMAIL_COPY"), parameters.get("SEND_EMAIL_WITH_COPY"));
            blindCopyEmails = emailsToSend(parameters.get("BLIND_COPY_EMAILS"), parameters.get("SEND_EMAIL_WITH_BLIND_COPY"));
        }

        byte[] file = null;
        String fileName = null;
        if(isSendAttachment){
            file = iEmployeePdfGeneratorService.generateEmployeeReport(employeeRequest,isUpdate);
            fileName = generateName(employeeRequest.getNames(), employeeRequest.getLastNames(), employeeRequest.getTypeDocument(), employeeRequest.getDocumentNumber());
            is3PdfStorageService.uploadPdf(file, fileName, employeeRequest.getDocumentNumber(), employeeRequest.getTypeDocument());
        }

        iMailDeliveryService.sendText(
                employeeRequest.getEmail(),
                subject,
                content,
                file,
                fileName,
                emailCopy,
                blindCopyEmails
        );
    }

    /**
     * Parses a comma-separated string of emails into a list, applying enable/disable rules.
     *
     * <p>
     * If {@code emailString} is null/blank or {@code paramVerPerm != "1"},
     * an empty list is returned.
     * </p>
     *
     * @param emailString  the comma-separated emails (e.g. {@code "a@test.com, b@test.com"})
     * @param paramVerPerm parameter indicating whether the emails should be applied ("1" enables them)
     * @return a list of normalized email addresses, or an empty list if not enabled
     */
    private List<String> emailsToSend(String emailString, String paramVerPerm) {
        List<String> emails = new ArrayList<>();
        if(!GeneralRestUtil.isNullOrBlank(emailString) && paramVerPerm.equals("1")){
            emails = Arrays.stream(emailString.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .toList();
        }
        return emails;
    }

    /**
     * Generates a unique PDF filename using parts of employee data and a random UUID.
     * <p>
     * The generated name follows the pattern:
     * <pre>
     * PDF-{namePrefix}{lastNamePrefix}-{docType}-{docNumberPrefix}-{uuid}.pdf
     * </pre>
     * </p>
     *
     * @param names          employee first names
     * @param lastNames      employee last names
     * @param typeDocument   document type code
     * @param documentNumber document number
     * @return a unique filename for the PDF report
     */
    private String generateName(
            String names,
            String lastNames,
            String typeDocument,
            String documentNumber
    ) {
        String namePart = safePrefix(names, 2);
        String lastNamePart = safePrefix(lastNames, 2);
        String docTypePart = safeUpper(typeDocument);
        String docNumberPart = safeDigitsPrefix(documentNumber, 3);
        String uuid = UUID.randomUUID().toString();

        return String.format(
                "PDF-%s%s-%s-%s-%s.pdf",
                namePart,
                lastNamePart,
                docTypePart,
                docNumberPart,
                uuid
        );
    }
}
