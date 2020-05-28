package com.mooctest.domainObject;

import lombok.Data;

import java.io.Serializable;

/**
 * @author guochao
 * @date 2020-05-11 15:28
 */
@Data
public class SuperPicture implements Serializable {
    private String textBefore;
    private String textAfter;
    private SuperParagraph paragraphBefore;
    private SuperParagraph paragraphAfter;
    private String suggestFileExtension;
    private String base64Content;
    private int dxaGoal;
    private int dyaGoal;
    private int paragraphID;
    private int index;

    // doc 没有
    private String fileName;

    public SuperPicture(String textBefore, String textAfter, SuperParagraph paragraphBefore, SuperParagraph paragraphAfter, String suggestFileExtension, String base64Content, int dxaGoal, int dyaGoal, int paragraphID, int index, String fileName) {
        this.textBefore = textBefore;
        this.textAfter = textAfter;
        this.paragraphBefore = paragraphBefore;
        this.paragraphAfter = paragraphAfter;
        this.suggestFileExtension = suggestFileExtension;
        this.base64Content = base64Content;
        this.dxaGoal = dxaGoal;
        this.dyaGoal = dyaGoal;
        this.paragraphID = paragraphID;
        this.index = index;
        this.fileName = fileName;
    }

    public SuperPicture() {

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

    public String getSuggestFileExtension() {
        return suggestFileExtension;
    }

    public void setSuggestFileExtension(String suggestFileExtension) {
        this.suggestFileExtension = suggestFileExtension;
    }

    public String getBase64Content() {
        return base64Content;
    }

    public void setBase64Content(String base64Content) {
        this.base64Content = base64Content;
    }

    public int getDxaGoal() {
        return dxaGoal;
    }

    public void setDxaGoal(int dxaGoal) {
        this.dxaGoal = dxaGoal;
    }

    public int getDyaGoal() {
        return dyaGoal;
    }

    public void setDyaGoal(int dyaGoal) {
        this.dyaGoal = dyaGoal;
    }

    public int getParagraphID() {
        return paragraphID;
    }

    public void setParagraphID(int paragraphID) {
        this.paragraphID = paragraphID;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
