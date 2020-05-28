package com.mooctest.domainObject.DocxParser;

import com.mooctest.domainObject.SuperTable;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class DocxTable extends SuperTable implements Serializable {
//    private String textBefore;
//    private String textAfter;
//    private DocxParagraph paragraphBefore;
//    private DocxParagraph paragraphAfter;
//    private int index;
    public List<List<List<DocxParagraph>>> docxTableContent = new ArrayList<>();

    public DocxTable(List<List<List<DocxParagraph>>> tableContent) {
        super();
        this.docxTableContent = tableContent;
    }

    public DocxTable() {
        super();
    }

    public List<List<List<DocxParagraph>>> getDocxTableContent() {
        return docxTableContent;
    }

    public void setDocxTableContent(List<List<List<DocxParagraph>>> docxTableContent) {
        this.docxTableContent = docxTableContent;
    }
}
