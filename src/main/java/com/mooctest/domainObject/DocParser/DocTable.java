package com.mooctest.domainObject.DocParser;

import com.mooctest.domainObject.SuperTable;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class DocTable extends SuperTable implements Serializable {
//    private String textBefore;
//    private String textAfter;
//    private DocParagraph paragraphBefore;
//    private DocParagraph paragraphAfter;
//    private int index;
    public List<List<List<DocParagraph>>> docTableContent = new ArrayList<>();

    public DocTable(List<List<List<DocParagraph>>> tableContent) {
        this.docTableContent = tableContent;
    }

    public DocTable() {

    }

    public List<List<List<DocParagraph>>> getDocTableContent() {
        return docTableContent;
    }

    public void setDocTableContent(List<List<List<DocParagraph>>> docTableContent) {
        this.docTableContent = docTableContent;
    }
}
