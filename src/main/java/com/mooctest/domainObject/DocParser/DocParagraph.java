package com.mooctest.domainObject.DocParser;

import com.mooctest.domainObject.SuperParagraph;
import lombok.Data;

import java.io.Serializable;
import java.math.BigInteger;

@Data
public class DocParagraph extends SuperParagraph implements Serializable {
//    private int lvl;
    private int llvl;
    private int linfo;
//    private int FontSize;
//    private String FontName;

//    private boolean isBold = false;
//    private boolean isItalic = false;
//    private int justification = 0;
//    private boolean isInTable = false;
    private String lineSpacing;
    private int fontAlignment;
//    private boolean isTableRowEnd = false;
//    private int paragraphID;
//    private int indentFromLeft;
//    private int indentFromRight;
//    private int firstLineIndent;
    private int underline;
    private String justification;  // 0=left, 1=center, 2=right, 3=left and right
    private boolean tableRowEnd = false;


//    private Paragraph paragraph = null;
//    private XWPFParagraph xwpfParagraph = null;


    public DocParagraph(String paragraphText, int lvl, int fontSize, String fontName, String asciiFontName, String eastAsiaFontName, boolean bold, boolean italic, boolean highlighted, boolean strike, boolean inTable, int paragraphID, double indentBeforeText, double indentAfterText, double firstLineIndent, double lineSpace, String color, int rowspan, int colspan, String numFmt, String numLevelText, BigInteger numIlvl, BigInteger numId, int llvl, int linfo, String lineSpacing, int fontAlignment, int underline, String justification, boolean tableRowEnd) {
        super(paragraphText, lvl, fontSize, fontName, asciiFontName, eastAsiaFontName, bold, italic, highlighted, strike, inTable, paragraphID, indentBeforeText, indentAfterText, firstLineIndent, lineSpace, color, rowspan, colspan, numFmt, numLevelText, numIlvl, numId);
        this.llvl = llvl;
        this.linfo = linfo;
        this.lineSpacing = lineSpacing;
        this.fontAlignment = fontAlignment;
        this.underline = underline;
        this.justification = justification;
        this.tableRowEnd = tableRowEnd;
    }

    public DocParagraph(int llvl, int linfo, String lineSpacing, int fontAlignment, int underline, String justification, boolean tableRowEnd) {
        this.llvl = llvl;
        this.linfo = linfo;
        this.lineSpacing = lineSpacing;
        this.fontAlignment = fontAlignment;
        this.underline = underline;
        this.justification = justification;
        this.tableRowEnd = tableRowEnd;
    }

    public DocParagraph() {

    }

    public int getLlvl() {
        return llvl;
    }

    public void setLlvl(int llvl) {
        this.llvl = llvl;
    }

    public int getLinfo() {
        return linfo;
    }

    public void setLinfo(int linfo) {
        this.linfo = linfo;
    }

    public String getLineSpacing() {
        return lineSpacing;
    }

    public void setLineSpacing(String lineSpacing) {
        this.lineSpacing = lineSpacing;
    }

    public int getFontAlignment() {
        return fontAlignment;
    }

    public void setFontAlignment(int fontAlignment) {
        this.fontAlignment = fontAlignment;
    }

    public int getUnderline() {
        return underline;
    }

    public void setUnderline(int underline) {
        this.underline = underline;
    }

    public String getJustification() {
        return justification;
    }

    public void setJustification(String justification) {
        this.justification = justification;
    }

    public boolean isTableRowEnd() {
        return tableRowEnd;
    }

    public void setTableRowEnd(boolean tableRowEnd) {
        this.tableRowEnd = tableRowEnd;
    }
}
