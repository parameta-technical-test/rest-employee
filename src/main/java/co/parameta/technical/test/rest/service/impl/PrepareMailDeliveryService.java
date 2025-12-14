package co.parameta.technical.test.rest.service.impl;

import co.parameta.technical.test.commons.dto.SystemParameterDTO;
import co.parameta.technical.test.commons.util.helper.GeneralUtil;
import co.parameta.technical.test.commons.util.mapper.SystemParameterMapper;
import co.parameta.technical.test.rest.dto.EmployeeRequestDTO;
import co.parameta.technical.test.rest.repository.SystemParameterRepository;
import co.parameta.technical.test.rest.service.IEmployeePdfGeneratorService;
import co.parameta.technical.test.rest.service.IMailDeliveryService;
import co.parameta.technical.test.rest.service.IPrepareMailDeliveryService;
import co.parameta.technical.test.rest.service.IS3PdfStorageService;
import co.parameta.technical.test.rest.util.helper.GeneralRestUtil;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static co.parameta.technical.test.rest.util.helper.GeneralRestUtil.*;

@Service
@RequiredArgsConstructor
public class PrepareMailDeliveryService implements IPrepareMailDeliveryService {

    private final SystemParameterMapper systemParameterMapper;
    private final IEmployeePdfGeneratorService iEmployeePdfGeneratorService;
    private final IMailDeliveryService iMailDeliveryService;
    private final SystemParameterRepository systemParameterRepository;
    private final IS3PdfStorageService is3PdfStorageService;

    @Override
    @Async
    public void prepareMailDelivery(EmployeeRequestDTO employeeRequest) throws MessagingException {
        Map<String, String> parameters =
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
        byte[] file = null;
        String fileName = null;
        if(parameters.get("EMAIL_SEND_ATTACHMENT").equals("1")){
            file = iEmployeePdfGeneratorService.generateEmployeeReport(employeeRequest);
            fileName = generateName(employeeRequest.getNames(), employeeRequest.getLastNames(), employeeRequest.getTypeDocument(), employeeRequest.getDocumentNumber());
            is3PdfStorageService.uploadPdf(file, fileName, employeeRequest.getDocumentNumber(), employeeRequest.getTypeDocument());
        }

        iMailDeliveryService.sendText(
                employeeRequest.getEmail(),
                parameters.get("EMAIL_SUBJECT"),
                parameters.get("EMAIL_CONTENT"),
                file,
                fileName,
                emailsToSend(parameters.get("EMAIL_COPY"), parameters.get("SEND_EMAIL_WITH_COPY")),
                emailsToSend(parameters.get("BLIND_COPY_EMAILS"), parameters.get("SEND_EMAIL_WITH_BLIND_COPY"))
        );
    }

    private List<String> emailsToSend(String emailString, String paramVerPerm){
        List<String> emails = new ArrayList<>();
        if(!GeneralRestUtil.isNullOrBlank(emailString)){
            if(paramVerPerm.equals("1")){
                emails = Arrays.stream(emailString.split(","))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .toList();
            }
        }
        return emails;
    }

    private static String generateName(
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
