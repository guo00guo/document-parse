package com.mooctest.domainObject;

import lombok.Data;
import org.apache.poi.xwpf.usermodel.UnderlinePatterns;

import java.io.Serializable;

/**
 * @author guochao
 * @date 2020-05-11 18:12
 */
@Data
public class SuperFontStyle implements Serializable {
    private int FontSize;
    private String FontName = "宋体";
    private String asciiFontName = "";
    private String eastAsiaFontName = "";
    private boolean isBold = false;   // 是否加粗
    private boolean isItalic = false;   // 是否斜体
    private boolean highlighted = false;    // 是否高亮
    private boolean strike = false; // 是否存在单删除线
    private UnderlinePatterns underline = UnderlinePatterns.NONE;   // 是否存在下划线
    private int fontAlignment;  // 字体对齐方式：1左对齐 2居中 3右对齐
    private String color = "000000";

    public int getFontSize() {
        return FontSize;
    }

    public void setFontSize(int fontSize) {
        FontSize = fontSize;
    }

    public String getFontName() {
        return FontName;
    }

    public void setFontName(String fontName) {
        FontName = fontName;
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
        return isBold;
    }

    public void setBold(boolean bold) {
        isBold = bold;
    }

    public boolean isItalic() {
        return isItalic;
    }

    public void setItalic(boolean italic) {
        isItalic = italic;
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

    public UnderlinePatterns getUnderline() {
        return underline;
    }

    public void setUnderline(UnderlinePatterns underline) {
        this.underline = underline;
    }

    public int getFontAlignment() {
        return fontAlignment;
    }

    public void setFontAlignment(int fontAlignment) {
        this.fontAlignment = fontAlignment;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
