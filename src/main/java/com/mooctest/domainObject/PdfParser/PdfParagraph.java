package main.java.com.mooctest.domainObject.PdfParser;

import main.java.com.mooctest.domainObject.SuperParagraph;
import lombok.Data;

import java.io.Serializable;

@Data
public class PdfParagraph extends SuperParagraph implements Serializable {
//    private String paragraphText = null;
//    private int paragraphID;
//    private String FontName = "宋体";
//    private String asciiFontName = "";
//    private boolean isBold = false;
//    private boolean isItalic = false;
//    private boolean isInTable = false;
//    private String color = "000000";
    private String PdfTitle;
    private int level = 9;

    public PdfParagraph(String pdfTitle, int level) {
        PdfTitle = pdfTitle;
        this.level = level;
    }

    public PdfParagraph() {

    }

    public String getPdfTitle() {
        return PdfTitle;
    }

    public void setPdfTitle(String pdfTitle) {
        PdfTitle = pdfTitle;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
