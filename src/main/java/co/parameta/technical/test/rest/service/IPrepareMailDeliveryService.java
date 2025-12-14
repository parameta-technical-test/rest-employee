package co.parameta.technical.test.rest.service;

import co.parameta.technical.test.rest.dto.EmployeeRequestDTO;
import jakarta.mail.MessagingException;

/**
 * Service interface responsible for preparing and triggering employee email delivery.
 * <p>
 * This service orchestrates the process of:
 * <ul>
 *     <li>Generating the employee PDF report</li>
 *     <li>Uploading the report to S3 (if enabled)</li>
 *     <li>Building the email content and recipients</li>
 *     <li>Sending the email with optional attachments</li>
 * </ul>
 * </p>
 */
public interface IPrepareMailDeliveryService {

    /**
     * Prepares and sends an email related to an employee process.
     * <p>
     * The email configuration (subject, content, CC, BCC, attachments)
     * is driven by system parameters.
     * </p>
     *
     * @param employeeRequest the employee information used to generate
     *                        the email content and attachments
     * @throws MessagingException if an error occurs during email preparation or delivery
     */
    void prepareMailDelivery(EmployeeRequestDTO employeeRequest) throws MessagingException;

}
