package co.parameta.technical.test.rest.service.impl;

import co.parameta.technical.test.rest.util.constant.Constants;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

/**
 * PDF page event handler that adds a simple footer to each page.
 * <p>
 * The footer displays the current page number aligned to the right
 * at the bottom of the document.
 */
@Service
public class SimpleFooterService extends PdfPageEventHelper {

    /**
     * Adds the footer content at the end of each PDF page.
     *
     * @param writer   the PDF writer associated with the document
     * @param document the current PDF document
     */
    @Override
    public void onEndPage(PdfWriter writer, Document document) {
        PdfPTable footer = new PdfPTable(1);
        try {
            footer.setTotalWidth(document.right() - document.left());

            PdfPCell cell = new PdfPCell(
                    new Phrase("Page " + writer.getPageNumber(), Constants.FOOTER_FONT)
            );
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);

            footer.addCell(cell);

            footer.writeSelectedRows(
                    0, -1,
                    document.left(),
                    document.bottom() - 10,
                    writer.getDirectContent()
            );
        } catch (Exception ignored) {
            // Footer rendering should not interrupt PDF generation
        }
    }
}
