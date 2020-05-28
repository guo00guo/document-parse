package com.mooctest.domainObject.DocParser;

import com.mooctest.domainObject.SuperParagraph;
import com.mooctest.domainObject.SuperPicture;
import lombok.Data;

import java.io.Serializable;

@Data
public class DocPicture extends SuperPicture implements Serializable {
//    private String textBefore;
//    private String textAfter;
    private int height;
    private int width;
    private int size;
//    private DocParagraph paragraphBefore;
//    private DocParagraph paragraphAfter;
//    private String suggestFileExtension;
//    private String base64Content;
//    private int dxaGoal;
//    private int dyaGoal;
//    private int index;
//    private String fileName;


    public DocPicture(String textBefore, String textAfter, SuperParagraph paragraphBefore, SuperParagraph paragraphAfter, String suggestFileExtension, String base64Content, int dxaGoal, int dyaGoal, int paragraphID, int index, String fileName, int height, int width, int size) {
        super(textBefore, textAfter, paragraphBefore, paragraphAfter, suggestFileExtension, base64Content, dxaGoal, dyaGoal, paragraphID, index, fileName);
        this.height = height;
        this.width = width;
        this.size = size;
    }

    public DocPicture(int height, int width, int size) {
        this.height = height;
        this.width = width;
        this.size = size;
    }

    public DocPicture() {

    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
