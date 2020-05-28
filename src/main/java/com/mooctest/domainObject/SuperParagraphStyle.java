package com.mooctest.domainObject;

import lombok.Data;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;

import java.io.Serializable;

/**
 * @author guochao
 * @date 2020-05-11 18:12
 */
@Data
public class SuperParagraphStyle implements Serializable {
    private int lvl;
    private int indentFromLeft;
    private int indentFromRight;
    private int firstLineIndent;    // 首行缩进
    private ParagraphAlignment alignment = ParagraphAlignment.BOTH; // 段落对齐方式

    public int getLvl() {
        return lvl;
    }

    public void setLvl(int lvl) {
        this.lvl = lvl;
    }

    public int getIndentFromLeft() {
        return indentFromLeft;
    }

    public void setIndentFromLeft(int indentFromLeft) {
        this.indentFromLeft = indentFromLeft;
    }

    public int getIndentFromRight() {
        return indentFromRight;
    }

    public void setIndentFromRight(int indentFromRight) {
        this.indentFromRight = indentFromRight;
    }

    public int getFirstLineIndent() {
        return firstLineIndent;
    }

    public void setFirstLineIndent(int firstLineIndent) {
        this.firstLineIndent = firstLineIndent;
    }

    public ParagraphAlignment getAlignment() {
        return alignment;
    }

    public void setAlignment(ParagraphAlignment alignment) {
        this.alignment = alignment;
    }
}
