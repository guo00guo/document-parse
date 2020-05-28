package com.mooctest.domainObject;

import lombok.Data;
import org.apache.poi.xwpf.usermodel.TableRowAlign;

import java.io.Serializable;

/**
 * @author guochao
 * @date 2020-05-11 15:46
 */
@Data
public class SuperTable implements Serializable {
    private String textBefore;
    private String textAfter;
    private SuperParagraph paragraphBefore;
    private SuperParagraph paragraphAfter;
    private int index;
    private TableRowAlign alignment;


    public SuperTable(String textBefore, String textAfter, SuperParagraph paragraphBefore, SuperParagraph paragraphAfter, int index) {
        this.textBefore = textBefore;
        this.textAfter = textAfter;
        this.paragraphBefore = paragraphBefore;
        this.paragraphAfter = paragraphAfter;
        this.index = index;
    }

    public SuperTable() {

    }

    public String getTextBefore() {
        return textBefore;
    }

    public void setTextBefore(String textBefore) {
        this.textBefore = textBefore;
    }

    public String getTextAfter() {
        return textAfter;
    }

    public void setTextAfter(String textAfter) {
        this.textAfter = textAfter;
    }

    public SuperParagraph getParagraphBefore() {
        return paragraphBefore;
    }

    public void setParagraphBefore(SuperParagraph paragraphBefore) {
        this.paragraphBefore = paragraphBefore;
    }

    public SuperParagraph getParagraphAfter() {
        return paragraphAfter;
    }

    public void setParagraphAfter(SuperParagraph paragraphAfter) {
        this.paragraphAfter = paragraphAfter;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public TableRowAlign getAlignment() {
        return alignment;
    }

    public void setAlignment(TableRowAlign alignment) {
        this.alignment = alignment;
    }
}
