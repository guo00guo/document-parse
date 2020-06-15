package com.mooctest.domainObject.DocxParser;

import com.mooctest.domainObject.SuperParagraph;
import lombok.Data;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.UnderlinePatterns;

import java.io.Serializable;
import java.math.BigInteger;

@Data
public class DocxParagraph extends SuperParagraph implements Serializable {
//    private String paragraphText = null;
//    private int lvl;    // 大纲级别
//    private int FontSize;
//    private String FontName = "宋体";
//    private String asciiFontName = "";
//    private String eastAsiaFontName = "";
//
//    private boolean isBold = false;   // 是否加粗
//    private boolean isItalic = false;   // 是否斜体
//    private boolean highlighted = false;    // 是否高亮
//    private boolean strike = false; // 是否存在单删除线
    private UnderlinePatterns underline = UnderlinePatterns.NONE;   // 是否存在下划线
//
//    private boolean isInTable = false;
//    private int lineSpacing;
//    private int fontAlignment;  // 字体对齐方式：1左对齐 2居中 3右对齐
    private String justification;  // 0=left, 1=center, 2=right, 3=left and right
    //    private boolean isTableRowEnd = false;
//    private int paragraphID;
//    private int indentFromLeft;
//    private int indentFromRight;
//    private int firstLineIndent;    // 首行缩进
//    private String color = "000000";
    private ParagraphAlignment alignment = ParagraphAlignment.BOTH; // 段落对齐方式
//
//    // 单元格合并信息
//    private int rowspan;    // 合并的行数
//    private int colspan;    // 合并的列数
//
//    private String numFmt;   // 标题格式
//    private String numLevelText;    // 标题模板
//    private BigInteger numIlvl;     // 标题深度
//    private BigInteger numId;     // 标题级别


    public DocxParagraph(String paragraphText, int lvl, int fontSize, String fontName, String asciiFontName, String eastAsiaFontName, boolean bold, boolean italic, boolean highlighted, boolean strike, boolean inTable, int paragraphID, double indentBeforeText, double indentAfterText, double firstLineIndent, double lineSpace, String color, int rowspan, int colspan, String numFmt, String numLevelText, BigInteger numIlvl, BigInteger numId, UnderlinePatterns underline, String justification, ParagraphAlignment alignment) {
        super(paragraphText, lvl, fontSize, fontName, asciiFontName, eastAsiaFontName, bold, italic, highlighted, strike, inTable, paragraphID, indentBeforeText, indentAfterText, firstLineIndent, lineSpace, color, rowspan, colspan, numFmt, numLevelText, numIlvl, numId);
        this.underline = underline;
        this.justification = justification;
        this.alignment = alignment;
    }

    public DocxParagraph(){

    }

    public UnderlinePatterns getUnderline() {
        return underline;
    }

    public void setUnderline(UnderlinePatterns underline) {
        this.underline = underline;
    }

    public String getJustification() {
        return justification;
    }

    public void setJustification(String justification) {
        this.justification = justification;
    }

    public ParagraphAlignment getAlignment() {
        return alignment;
    }

    public void setAlignment(ParagraphAlignment alignment) {
        this.alignment = alignment;
    }
}
