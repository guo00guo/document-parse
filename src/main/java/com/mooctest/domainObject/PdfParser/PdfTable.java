package main.java.com.mooctest.domainObject.PdfParser;

import main.java.com.mooctest.domainObject.SuperTable;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


@Data
public class PdfTable extends SuperTable implements Serializable {
//    private String textBefore;
//    private String textAfter;
//    private PdfParagraph paragraphBefore;
//    private PdfParagraph paragraphAfter;
//    private int index;
    public List<List<List<PdfParagraph>>> pdfTableContent = new ArrayList<>();

    public PdfTable(List<List<List<PdfParagraph>>> tableContent) {
        this.pdfTableContent = tableContent;
    }

    public List<List<List<PdfParagraph>>> getPdfTableContent() {
        return pdfTableContent;
    }

    public void setPdfTableContent(List<List<List<PdfParagraph>>> pdfTableContent) {
        this.pdfTableContent = pdfTableContent;
    }
}
