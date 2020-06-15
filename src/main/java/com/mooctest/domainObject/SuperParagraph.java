package com.mooctest.domainObject;

import lombok.Data;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;

import java.io.Serializable;
import java.math.BigInteger;

/**
 * @author guochao
 * @date 2020-05-11 15:40
 */
@Data
public class SuperParagraph implements Serializable {
    private String paragraphText = null;
    private int lvl;    // 大纲级别
    private int fontSize;
    private String fontName = "宋体";
    private String asciiFontName = "";
    private String eastAsiaFontName = "";

    private boolean bold = false;   // 是否加粗
    private boolean italic = false;   // 是否斜体
    private boolean highlighted = false;    // 是否高亮
    private boolean strike = false; // 是否存在单删除线

    // doc 有 docx 没有
//    private String justification;  // 0=left, 1=center, 2=right, 3=left and right
//    private boolean tableRowEnd = false;

    private boolean inTable = false;
    private int paragraphID;
    private double indentBeforeText;    // 文本之前缩进
    private double indentAfterText;     // 文本之后缩进
    private double firstLineIndent;     // 首行缩进
    private double lineSpace;           // 行距
    private String color = "000000";

    // 单元格合并信息
    private int rowspan;    // 合并的行数
    private int colspan;    // 合并的列数

    // doc 没有
    private String numFmt;   // 标题格式
    private String numLevelText;    // 标题模板
    private BigInteger numIlvl;     // 标题深度
    private BigInteger numId;     // 标题级别
//    private ParagraphAlignment alignment = ParagraphAlignment.BOTH; // 段落对齐方式
//    private UnderlinePatterns underline = UnderlinePatterns.NONE;   // 是否存在下划线

    public SuperParagraph(String paragraphText, int lvl, int fontSize, String fontName, String asciiFontName, String eastAsiaFontName, boolean bold, boolean italic, boolean highlighted, boolean strike, boolean inTable, int paragraphID, double indentBeforeText, double indentAfterText, double firstLineIndent, double lineSpace, String color, int rowspan, int colspan, String numFmt, String numLevelText, BigInteger numIlvl, BigInteger numId) {
        this.paragraphText = paragraphText;
        this.lvl = lvl;
        this.fontSize = fontSize;
        this.fontName = fontName;
        this.asciiFontName = asciiFontName;
        this.eastAsiaFontName = eastAsiaFontName;
        this.bold = bold;
        this.italic = italic;
        this.highlighted = highlighted;
        this.strike = strike;
        this.inTable = inTable;
        this.paragraphID = paragraphID;
        this.indentBeforeText = indentBeforeText;
        this.indentAfterText = indentAfterText;
        this.firstLineIndent = firstLineIndent;
        this.lineSpace = lineSpace;
        this.color = color;
        this.rowspan = rowspan;
        this.colspan = colspan;
        this.numFmt = numFmt;
        this.numLevelText = numLevelText;
        this.numIlvl = numIlvl;
        this.numId = numId;
    }

    public SuperParagraph() {
    }

    public String getParagraphText() {
        return paragraphText;
    }

    public void setParagraphText(String paragraphText) {
        this.paragraphText = paragraphText;
    }

    public int getLvl() {
        return lvl;
    }

    public void setLvl(int lvl) {
        this.lvl = lvl;
    }

    public int getFontSize() {
        return fontSize;
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }

    public String getFontName() {
        return fontName;
    }

    public void setFontName(String fontName) {
        this.fontName = fontName;
    }

    public String getAsciiFontName() {
        return asciiFontName;
    }

    public void setAsciiFontName(String asciiFontName) {
        this.asciiFontName = asciiFontName;
    }

    public String getEastAsiaFontName() {
        return eastAsiaFontName;
    }

    public void setEastAsiaFontName(String eastAsiaFontName) {
        this.eastAsiaFontName = eastAsiaFontName;
    }

    public boolean isBold() {
        return bold;
    }

    public void setBold(boolean bold) {
        this.bold = bold;
    }

    public boolean isItalic() {
        return italic;
    }

    public void setItalic(boolean italic) {
        this.italic = italic;
    }

    public boolean isHighlighted() {
        return highlighted;
    }

    public void setHighlighted(boolean highlighted) {
        this.highlighted = highlighted;
    }

    public boolean isStrike() {
        return strike;
    }

    public void setStrike(boolean strike) {
        this.strike = strike;
    }

    public boolean isInTable() {
        return inTable;
    }

    public void setInTable(boolean inTable) {
        this.inTable = inTable;
    }

    public int getParagraphID() {
        return paragraphID;
    }

    public void setParagraphID(int paragraphID) {
        this.paragraphID = paragraphID;
    }

    public double getIndentBeforeText() {
        return indentBeforeText;
    }

    public void setIndentBeforeText(double indentBeforeText) {
        this.indentBeforeText = indentBeforeText;
    }

    public double getIndentAfterText() {
        return indentAfterText;
    }

    public void setIndentAfterText(double indentAfterText) {
        this.indentAfterText = indentAfterText;
    }

    public double getFirstLineIndent() {
        return firstLineIndent;
    }

    public void setFirstLineIndent(double firstLineIndent) {
        this.firstLineIndent = firstLineIndent;
    }

    public double getLineSpace() {
        return lineSpace;
    }

    public void setLineSpace(double lineSpace) {
        this.lineSpace = lineSpace;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getRowspan() {
        return rowspan;
    }

    public void setRowspan(int rowspan) {
        this.rowspan = rowspan;
    }

    public int getColspan() {
        return colspan;
    }

    public void setColspan(int colspan) {
        this.colspan = colspan;
    }

    public String getNumFmt() {
        return numFmt;
    }

    public void setNumFmt(String numFmt) {
        this.numFmt = numFmt;
    }

    public String getNumLevelText() {
        return numLevelText;
    }

    public void setNumLevelText(String numLevelText) {
        this.numLevelText = numLevelText;
    }

    public BigInteger getNumIlvl() {
        return numIlvl;
    }

    public void setNumIlvl(BigInteger numIlvl) {
        this.numIlvl = numIlvl;
    }

    public BigInteger getNumId() {
        return numId;
    }

    public void setNumId(BigInteger numId) {
        this.numId = numId;
    }
}
