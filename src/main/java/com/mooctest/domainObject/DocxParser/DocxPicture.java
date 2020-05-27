package main.java.com.mooctest.domainObject.DocxParser;

import main.java.com.mooctest.domainObject.SuperParagraph;
import main.java.com.mooctest.domainObject.SuperPicture;
import lombok.Data;

import java.io.Serializable;

@Data
public class DocxPicture extends SuperPicture implements Serializable {
//    private String textBefore;
//    private String textAfter;
//    private DocxParagraph paragraphBefore;
//    private DocxParagraph paragraphAfter;
//    private double height;
//    private double width;
//    private String suggestFileExtension;
//    private String base64Content;
//    private int dxaGoal;
//    private int dyaGoal;
//    private int paragraphID;
//    private String fileName;
//    private int index;

    public DocxPicture(String textBefore, String textAfter, SuperParagraph paragraphBefore, SuperParagraph paragraphAfter, String suggestFileExtension, String base64Content, int dxaGoal, int dyaGoal, int paragraphID, int index, String fileName) {
        super(textBefore, textAfter, paragraphBefore, paragraphAfter, suggestFileExtension, base64Content, dxaGoal, dyaGoal, paragraphID, index, fileName);
    }

    public DocxPicture() {
        super();

    }


}
