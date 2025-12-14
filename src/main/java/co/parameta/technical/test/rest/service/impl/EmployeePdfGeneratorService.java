package co.parameta.technical.test.rest.service.impl;

import co.parameta.technical.test.rest.dto.EmployeeRequestDTO;
import co.parameta.technical.test.rest.service.IEmployeePdfGeneratorService;
import lombok.RequiredArgsConstructor;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.draw.LineSeparator;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class EmployeePdfGeneratorService implements IEmployeePdfGeneratorService {

    @Override
    public byte[] generateEmployeeReport(EmployeeRequestDTO employee) {
        if (employee == null) {
            throw new IllegalArgumentException("EmployeeRequestDTO cannot be null");
        }

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            Document document = new Document(PageSize.LETTER, 36, 36, 54, 54);
            PdfWriter.getInstance(document, baos);

            document.open();

            addHeader(document);
            addSubHeader(document, employee);
            addEmployeeTable(document, employee);

            document.add(Chunk.NEWLINE);
            addNotes(document);

            document.close();
            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error generating employee PDF", e);
        }
    }

    private static void addHeader(Document document) throws DocumentException {
        Font titleFont = new Font(Font.HELVETICA, 16, Font.BOLD);
        Font subtitleFont = new Font(Font.HELVETICA, 10, Font.NORMAL, new Color(90, 90, 90));

        Paragraph title = new Paragraph("Employee Registration Summary", titleFont);
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

    private static void addSubHeader(Document document, EmployeeRequestDTO employee)
            throws DocumentException {

        Font strong = new Font(Font.HELVETICA, 11, Font.BOLD);
        Font normal = new Font(Font.HELVETICA, 11, Font.NORMAL);

        Paragraph p = new Paragraph();
        p.add(new Chunk("Employee: ", strong));
        p.add(new Chunk(
                safe(employee.getNames()) + " " + safe(employee.getLastNames()),
                normal
        ));
        p.setSpacingAfter(12f);

        document.add(p);
    }

    private static void addEmployeeTable(Document document, EmployeeRequestDTO employee)
            throws DocumentException {

        PdfPTable table = new PdfPTable(new float[]{1.2f, 2.8f});
        table.setWidthPercentage(100);
        table.setSpacingBefore(6f);
        table.setSpacingAfter(8f);

        addRow(table, "Type of Document", safe(employee.getTypeDocument()));
        addRow(table, "Document Number", safe(employee.getDocumentNumber()));
        addRow(table, "Date of Birth", safe(employee.getDateOfBirth()));
        addRow(table, "Company Affiliation Date", safe(employee.getDateAffiliationCompany()));
        addRow(table, "Position", safe(employee.getPosition()));
        addRow(table, "Email", safe(employee.getEmail()));
        addRow(table, "Salary", formatMoney(employee.getSalary()));

        document.add(table);
    }

    private static void addNotes(Document document) throws DocumentException {
        Font noteTitle = new Font(Font.HELVETICA, 11, Font.BOLD);
        Font noteText = new Font(Font.HELVETICA, 10, Font.NORMAL, new Color(70, 70, 70));

        Paragraph title = new Paragraph("Notes", noteTitle);
        title.setSpacingAfter(6f);
        document.add(title);

        Paragraph content = new Paragraph(
                "This document is a summary of the employee information registered in the system. " +
                        "If you find any inconsistency, please contact the administrator or the HR team.",
                noteText
        );
        content.setLeading(14f);

        document.add(content);
    }

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

    private static String safe(String value) {
        return (value == null || value.trim().isEmpty()) ? "N/A" : value.trim();
    }

    private static String formatMoney(BigDecimal salary) {
        if (salary == null) return "N/A";
        NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("es", "CO"));
        return nf.format(salary);
    }
}

