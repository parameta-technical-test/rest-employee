package co.parameta.technical.test.rest.service.impl;

import co.parameta.technical.test.commons.util.exception.MensajePersonalizadoException;
import co.parameta.technical.test.rest.dto.EmployeeRequestDTO;
import co.parameta.technical.test.rest.repository.PositionRepository;
import co.parameta.technical.test.rest.repository.TypeDocumentRepository;
import co.parameta.technical.test.rest.service.IEmployeePdfGeneratorService;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.draw.LineSeparator;
import lombok.RequiredArgsConstructor;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Service implementation responsible for generating employee PDF reports.
 * <p>
 * Supports two report modes:
 * <ul>
 *   <li><b>Creation report</b>: when an employee is registered/created.</li>
 *   <li><b>Update report</b>: when an employee record is updated.</li>
 * </ul>
 * The report mode is controlled by a boolean flag {@code isUpdate}.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class EmployeePdfGeneratorService implements IEmployeePdfGeneratorService {

    /**
     * Repository used to manage and retrieve employee position data.
     * <p>
     * This repository provides access to position-related persistence operations,
     * such as finding position information by code or retrieving available
     * positions from the data source.
     * </p>
     */
    private final PositionRepository positionRepository;

    /**
     * Repository used to manage and retrieve document type data.
     * <p>
     * This repository provides access to document type persistence operations,
     * allowing validation and lookup of identification document types
     * associated with employees.
     * </p>
     */
    private final TypeDocumentRepository typeDocumentRepository;



    /**
     * Generates a PDF report containing employee information.
     * <p>
     * If {@code isUpdate} is {@code true}, the PDF is generated as an <b>update report</b>.
     * Otherwise, it is generated as a <b>creation report</b>.
     * </p>
     *
     * @param employee employee information used to populate the report
     * @param isUpdate {@code true} for an update report, {@code false} for a creation report
     * @return a byte array representing the generated PDF document
     * @throws IllegalArgumentException if {@code employee} is {@code null}
     * @throws RuntimeException if an error occurs during PDF generation
     */
    @Override
    public byte[] generateEmployeeReport(EmployeeRequestDTO employee, boolean isUpdate) {
        if (employee == null) {
            throw new IllegalArgumentException("EmployeeRequestDTO cannot be null");
        }

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            Document document = new Document(PageSize.LETTER, 36, 36, 54, 54);
            PdfWriter.getInstance(document, baos);

            document.open();

            addHeader(document, isUpdate);
            addOperationMeta(document, isUpdate);
            addSubHeader(document, employee);
            addEmployeeTable(document, employee);

            document.add(Chunk.NEWLINE);
            addOperationSummary(document, isUpdate);

            document.add(Chunk.NEWLINE);
            addNotes(document, isUpdate);

            document.close();
            return baos.toByteArray();

        } catch (Exception e) {
            throw new MensajePersonalizadoException("Error generating employee PDF", e);
        }
    }



    /**
     * Adds the header section depending on the report type.
     *
     * @param document PDF document
     * @param isUpdate flag indicating update mode
     * @throws DocumentException if writing fails
     */
    private void addHeader(Document document, boolean isUpdate) throws DocumentException {
        Font titleFont = new Font(Font.HELVETICA, 16, Font.BOLD);
        Font subtitleFont = new Font(Font.HELVETICA, 10, Font.NORMAL, new Color(90, 90, 90));

        String titleText = isUpdate
                ? "Updated Employee Information Summary"
                : "Employee Registration Summary";

        Paragraph title = new Paragraph(titleText, titleFont);
        title.setSpacingAfter(4f);
        document.add(title);

        String now = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        Paragraph subtitle = new Paragraph("Generated on: " + now, subtitleFont);
        subtitle.setSpacingAfter(16f);
        document.add(subtitle);

        LineSeparator separator = new LineSeparator();
        separator.setLineWidth(1f);
        separator.setLineColor(new Color(210, 210, 210));
        document.add(separator);

        document.add(Chunk.NEWLINE);
    }

    /**
     * Adds basic metadata describing the operation (create/update).
     *
     * @param document PDF document
     * @param isUpdate flag indicating update mode
     * @throws DocumentException if writing fails
     */
    private void addOperationMeta(Document document, boolean isUpdate) throws DocumentException {
        Font strong = new Font(Font.HELVETICA, 10, Font.BOLD);
        Font normal = new Font(Font.HELVETICA, 10, Font.NORMAL, new Color(55, 65, 81));

        String op = isUpdate ? "Employee data update" : "Employee registration";
        String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        Paragraph meta = new Paragraph();
        meta.setSpacingAfter(10f);

        meta.add(new Chunk("Operation: ", strong));
        meta.add(new Chunk(op, normal));
        meta.add(Chunk.NEWLINE);

        meta.add(new Chunk(isUpdate ? "Updated on: " : "Registered on: ", strong));
        meta.add(new Chunk(ts, normal));

        document.add(meta);
    }

    /**
     * Adds a sub-header section containing the employee full name.
     *
     * @param document PDF document
     * @param employee employee data
     * @throws DocumentException if writing fails
     */
    private void addSubHeader(Document document, EmployeeRequestDTO employee)
            throws DocumentException {

        Font strong = new Font(Font.HELVETICA, 11, Font.BOLD);
        Font normal = new Font(Font.HELVETICA, 11, Font.NORMAL);

        Paragraph p = new Paragraph();
        p.add(new Chunk("Employee: ", strong));
        p.add(new Chunk(safe(employee.getNames()) + " " + safe(employee.getLastNames()), normal));
        p.setSpacingAfter(12f);

        document.add(p);
    }

    /**
     * Adds the employee information table (latest values).
     *
     * @param document PDF document
     * @param employee employee data
     * @throws DocumentException if writing fails
     */
    private void addEmployeeTable(Document document, EmployeeRequestDTO employee)
            throws DocumentException {

        PdfPTable table = new PdfPTable(new float[]{1.2f, 2.8f});
        table.setWidthPercentage(100);
        table.setSpacingBefore(6f);
        table.setSpacingAfter(8f);

        addRow(table, "Type of Document", typeDocumentRepository.documentDescription(safe(employee.getTypeDocument())));
        addRow(table, "Document Number", safe(employee.getDocumentNumber()));
        addRow(table, "Date of Birth", safe(employee.getDateOfBirth()));
        addRow(table, "Company Affiliation Date", safe(employee.getDateAffiliationCompany()));
        addRow(table, "Position", positionRepository.positionDescription(safe(employee.getPosition())));
        addRow(table, "Email", safe(employee.getEmail()));
        addRow(table, "Salary", formatMoney(Double.parseDouble(employee.getSalary())));

        document.add(table);
    }




    /**
     * Adds a small summary section depending on the operation type.
     *
     * @param document PDF document
     * @param isUpdate update mode flag
     * @throws DocumentException if writing fails
     */
    private static void addOperationSummary(Document document, boolean isUpdate) throws DocumentException {
        Font sectionTitle = new Font(Font.HELVETICA, 11, Font.BOLD);
        Font text = new Font(Font.HELVETICA, 10, Font.NORMAL, new Color(70, 70, 70));

        String title = isUpdate ? "Update Summary" : "Registration Summary";
        String body = isUpdate
                ? "This report reflects the most recent employee information after an update operation. " +
                "Only the latest values are displayed in the summary table above."
                : "This report confirms the employee registration in the system. " +
                "The information shown in the summary table above corresponds to the registered values.";

        Paragraph t = new Paragraph(title, sectionTitle);
        t.setSpacingAfter(6f);
        document.add(t);

        Paragraph c = new Paragraph(body, text);
        c.setLeading(14f);
        document.add(c);
    }

    /**
     * Adds notes depending on whether the report is for create or update.
     *
     * @param document PDF document
     * @param isUpdate update mode flag
     * @throws DocumentException if writing fails
     */
    private static void addNotes(Document document, boolean isUpdate) throws DocumentException {
        Font noteTitle = new Font(Font.HELVETICA, 11, Font.BOLD);
        Font noteText = new Font(Font.HELVETICA, 10, Font.NORMAL, new Color(70, 70, 70));

        Paragraph title = new Paragraph("Notes", noteTitle);
        title.setSpacingAfter(6f);
        document.add(title);

        String msg = isUpdate
                ? "This document confirms that the employee record was updated in the system. " +
                "If you identify any inconsistency, please contact the system administrator or the Human Resources team."
                : "This document is a summary of the employee information registered in the system. " +
                "If you identify any inconsistency, please contact the system administrator or the Human Resources team.";

        Paragraph content = new Paragraph(msg, noteText);
        content.setLeading(14f);
        document.add(content);
    }

    /**
     * Adds a single row to the employee information table.
     *
     * @param table table reference
     * @param label label (left column)
     * @param value value (right column)
     */
    private static void addRow(PdfPTable table, String label, String value) {
        Font labelFont = new Font(Font.HELVETICA, 10, Font.BOLD, new Color(30, 30, 30));
        Font valueFont = new Font(Font.HELVETICA, 10, Font.NORMAL, new Color(30, 30, 30));

        PdfPCell c1 = new PdfPCell(new Phrase(label, labelFont));
        c1.setBackgroundColor(new Color(245, 246, 248));
        c1.setPadding(8f);
        c1.setBorderColor(new Color(220, 220, 220));

        PdfPCell c2 = new PdfPCell(new Phrase(value, valueFont));
        c2.setPadding(8f);
        c2.setBorderColor(new Color(220, 220, 220));

        table.addCell(c1);
        table.addCell(c2);
    }

    /**
     * Safely formats a string value for display.
     *
     * @param value raw value
     * @return trimmed value or "N/A"
     */
    private static String safe(String value) {
        return (value == null || value.trim().isEmpty()) ? "N/A" : value.trim();
    }

    /**
     * Formats money in Colombian locale.
     *
     * @param salary salary amount
     * @return formatted salary or "N/A"
     */
    private static String formatMoney(BigDecimal salary) {
        if (salary == null) return "N/A";
        NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("es", "CO"));
        return nf.format(salary);
    }

    /**
     * Formats money in Colombian locale.
     *
     * @param salary salary amount
     * @return formatted salary or "N/A"
     */
    private static String formatMoney(Double salary) {
        if (salary == null) {
            return "N/A";
        }
        return formatMoney(BigDecimal.valueOf(salary));
    }

}
