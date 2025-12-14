package co.parameta.technical.test.rest.service;

import co.parameta.technical.test.rest.service.impl.SimpleFooterService;
import com.lowagie.text.Document;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfWriter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SimpleFooterServiceTest {

    @Mock
    private PdfWriter writer;

    @Mock
    private Document document;

    @Mock
    private PdfContentByte pdfContentByte;

    @Test
    void onEndPageDoesNotThrowException() {
        SimpleFooterService footerService = new SimpleFooterService();

        when(writer.getPageNumber()).thenReturn(1);
        when(writer.getDirectContent()).thenReturn(pdfContentByte);

        when(document.right()).thenReturn(600f);
        when(document.left()).thenReturn(36f);
        when(document.bottom()).thenReturn(36f);

        assertDoesNotThrow(() ->
                footerService.onEndPage(writer, document)
        );

        verify(writer, times(1)).getPageNumber();
        verify(writer, times(1)).getDirectContent();
        verify(document, atLeastOnce()).left();
        verify(document, atLeastOnce()).right();
        verify(document, atLeastOnce()).bottom();
    }
}