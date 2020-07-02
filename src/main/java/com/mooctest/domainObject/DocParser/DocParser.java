package com.mooctest.domainObject.DocParser;

import com.google.common.base.CharMatcher;
import com.google.common.collect.Lists;
import com.mooctest.data.enums.JustificationEnum;
import com.mooctest.domainObject.SuperParagraph;
import com.mooctest.domainObject.SuperPicture;
import com.mooctest.domainObject.SuperTable;
import lombok.Data;
import org.apache.commons.codec.binary.Base64;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.hwpf.model.PicturesTable;
import org.apache.poi.hwpf.usermodel.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class DocParser implements Serializable {
    private transient File file;
    private transient InputStream stream = null;
    private transient HWPFDocument document = null;
    private List<DocParagraph> docParagraphs = new ArrayList<DocParagraph>();
    private List<DocTable> docTables = new ArrayList<DocTable>();
    private List<DocPicture> docPictures = new ArrayList<DocPicture>();
    private int docTableNum = 0;

    private transient int indent = 576;
    private transient int firstLineIndex = 576;
    public DocParser(){}

    public DocParser(List<DocParagraph> docParagraphs, List<DocTable> docTables, List<DocPicture> docPictures, int docTableNum) {
        this.docParagraphs = docParagraphs;
        this.docTables = docTables;
        this.docPictures = docPictures;
        this.docTableNum = docTableNum;
    }

    public DocParser(File file) {
        this.file = file;
        this.loadFile();
        this.processContent();
        this.processPicture();
    }


    protected void finalize() {
        if (null != this.document) {
            try {
                this.document.close(); //xwpf
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (null != this.stream) {
            try {
                this.stream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void loadFile() {
        try {
            this.stream = new FileInputStream(this.file);
            this.document = new HWPFDocument(this.stream);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private DocParagraph processParagraph(Paragraph paragraph, int index) {
        //解析段落信息
        DocParagraph docParagraph = new DocParagraph();
        // 设置文本之前缩进
        if(paragraph.getIndentFromLeft() == -1){
            docParagraph.setIndentBeforeText(0);
        }else{
            docParagraph.setIndentBeforeText((double) Math.round(paragraph.getIndentFromLeft() * 1.0 / indent * 100) / 100);
        }

        // 设置文本之后缩进
        if(paragraph.getIndentFromRight() == -1){
            docParagraph.setIndentAfterText(0);
        }else{
            docParagraph.setIndentAfterText((double) Math.round(paragraph.getIndentFromRight() * 1.0 / indent * 100) / 100);
        }

        // 设置首行缩进信息
        if(paragraph.getFirstLineIndent() == -1){
            docParagraph.setFirstLineIndent(0);
        }else{
            if(docParagraph.getIndentAfterText() == 0.0 && docParagraph.getIndentBeforeText() == 0.0){
                docParagraph.setFirstLineIndent((double) Math.round(paragraph.getFirstLineIndent() * 1.0 / indent * 100) / 100);
            }else{
                docParagraph.setFirstLineIndent((double) Math.round(paragraph.getFirstLineIndent() * 1.0 / firstLineIndex * 100) / 100);
            }
        }


//        docParagraph.setFirstLineIndent(paragraph.getFirstLineIndent());
        docParagraph.setFontAlignment(paragraph.getFontAlignment());
//        docParagraph.setIndentBeforeText(paragraph.getIndentFromLeft());
//        docParagraph.setIndentAfterText(paragraph.getIndentFromRight());
        docParagraph.setLvl(paragraph.getLvl());
        docParagraph.setLlvl(paragraph.getIlvl());
        docParagraph.setLinfo(paragraph.getIlfo());
        docParagraph.setParagraphText(CharMatcher.whitespace().removeFrom(WordExtractor.stripFields(paragraph.text())).trim());
//        docParagraph.setParagraph(paragraph);
        docParagraph.setInTable(paragraph.isInTable());
        docParagraph.setLineSpacing(paragraph.getLineSpacing().toString());
        docParagraph.setTableRowEnd(paragraph.isTableRowEnd());
        docParagraph.setJustification(JustificationEnum.getJustification(paragraph.getJustification()));
        docParagraph.setParagraphID(index);

        //解析字体格式等
        String fontName = "";
        int color = 0;
        int fontSize = -1;
        boolean isBold = false;
        boolean isItalic = false;
        boolean highlighted = false;
        boolean strike = false;
        int underline = 0;
        CharacterRun run = null;
        for (int i = 0; i < paragraph.numCharacterRuns(); i++) {
            run = paragraph.getCharacterRun(i);

            if (i == 0) {
                fontSize = run.getFontSize();
                fontName = run.getFontName();
                isBold = run.isBold();
                isItalic = run.isItalic();
                highlighted = run.isHighlighted();
                underline = run.getUnderlineCode();
                strike = run.isStrikeThrough();
                color = run.getColor();
            } else {
                if (fontSize != run.getFontSize()) {
                    fontSize = -1;
                }
                if (!fontName.equals(run.getFontName())) {
                    fontName = "";
                }
                if (isBold != run.isBold()) {
                    isBold = false;
                }
                if (isItalic != run.isItalic()) {
                    isItalic = false;
                }
                if (highlighted != run.isHighlighted()) highlighted = false;
                if (underline != run.getUnderlineCode()) underline = 0;
                if (strike != run.isStrikeThrough()) strike = false;
                if (color != run.getColor()) color = 0;
            }
        }
        docParagraph.setFontSize(fontSize);
        docParagraph.setFontName(fontName);
        docParagraph.setBold(isBold);
        docParagraph.setItalic(isItalic);
        docParagraph.setHighlighted(highlighted);
        docParagraph.setUnderline(underline);
        docParagraph.setStrike(strike);
        docParagraph.setColor(String.valueOf(color));
        return docParagraph;
    }

    private DocTable processTable(Range range, int start, int end) {

        DocTable docTable = new DocTable();
        docTable.setIndex(this.docTableNum + 1);
        if (start != 0) {
            docTable.setParagraphBefore(this.docParagraphs.get(this.docParagraphs.size() - 1));
            docTable.setTextBefore(docTable.getParagraphBefore().getParagraphText());
        }

        if (end < range.numParagraphs()) {
            docTable.setParagraphAfter(this.processParagraph(range.getParagraph(end), end));
            docTable.setTextAfter(docTable.getParagraphAfter().getParagraphText());
        }

        TableIterator it = new TableIterator(range);
        // 迭代文档中的表格
        int tableNum = 0;
        Table table = null;
        while (it.hasNext()) {
            if(tableNum != this.docTableNum){
                tableNum++;
                it.next();
                continue;
            }else{
                table = it.next();
                break;
            }
        }

        int index = start;
        //迭代行，默认从0开始,可以依据需要设置i的值,改变起始行数，也可设置读取到那行，只需修改循环的判断条件即可
        for (int i = 0; i < table.numRows(); i++) {
            TableRow tableRow = table.getRow(i);

            List<List<DocParagraph>> xtableRow = new ArrayList<>();
            //迭代列，默认从0开始
            for (int j = 0; j < tableRow.numCells(); j++) {
                TableCell tableCell = tableRow.getCell(j);//取得单元格
                //取得单元格的内容
                List<DocParagraph> paragraphsCell = new ArrayList<>();
                for(int k = 0; k < tableCell.numParagraphs(); k++){
                    Paragraph paragraph = tableCell.getParagraph(k);
                    DocParagraph docParagraph = this.processParagraph(paragraph, index);
                    paragraphsCell.add(docParagraph);
                    index++;
                }
                if(paragraphsCell.size()>0)
                    xtableRow.add(paragraphsCell);
            }
            if(xtableRow.size()>0)
                docTable.docTableContent.add(xtableRow);
        }
        docTableNum++;
        return docTable;
    }


    private void processPicture() {
        PicturesTable picturesTable = this.document.getPicturesTable();
        int length = document.characterLength();
        List<String> pictureTextBefore = Lists.newArrayList();
        List<String> pictureTextAfter = Lists.newArrayList();
        List<SuperParagraph> paragraphBeforeList = Lists.newArrayList();
        List<SuperParagraph> paragraphAfterList = Lists.newArrayList();
        List<Picture> pictures = picturesTable.getAllPictures();

        try {
            for (int i = 0; i < length; i++) {
                Range range = new Range(i, i + 1, document);

                CharacterRun characterRun = range.getCharacterRun(0);
                if (picturesTable.hasPicture(characterRun)) {
                    Range preRange;
                    if (i - 1000 >= 0) {
                        preRange = new Range(i - 1000, i, document);
                    } else {
                        preRange = new Range(0, i, document);
                    }
                    Range nextRange;
                    if (i + 1000 < length) {
                        nextRange = new Range(i + 2, i + 1000, document);
                    } else {
                        nextRange = new Range(i + 2, length - 1, document);
                    }
                    Paragraph preparagraph, nextparagraph;
                    if (preRange.numParagraphs() >= 3) {
                        preparagraph = preRange.getParagraph(preRange.numParagraphs()-1);
                    } else {
                        preparagraph = preRange.getParagraph(0);
                    }
                    if (nextRange.numParagraphs() > 1) {
                        nextparagraph = nextRange.getParagraph(0);
                    } else {
                        nextparagraph = nextRange.getParagraph(0);
                    }
                    String textBefore = preparagraph.text();
                    pictureTextBefore.add(textBefore);
                    String textAfter = nextparagraph.text();
                    pictureTextAfter.add(textAfter);
                }
            }

            int pictureNO = 0;
            for (Picture picture : pictures) {
                DocPicture docPicture = new DocPicture();
                docPicture.setHeight(picture.getHeight());
                docPicture.setWidth(picture.getWidth());
                docPicture.setSize(picture.getSize());
                docPicture.setTextBefore(CharMatcher.whitespace().removeFrom(WordExtractor.stripFields(pictureTextBefore.get(pictureNO))).trim());
                docPicture.setTextAfter(CharMatcher.whitespace().removeFrom(WordExtractor.stripFields(pictureTextAfter.get(pictureNO))).trim());
                docPicture.setSuggestFileExtension(picture.suggestFileExtension());
                String base64 = Base64.encodeBase64String(picture.getContent());
                docPicture.setBase64Content(base64);
                this.docPictures.add(docPicture);
                pictureNO += 1;
            }
        } catch (Exception e) {
            // 捕捉到异常，
            e.printStackTrace();
        }
    }


//    private void processPicture() {
//
//        PicturesTable picturesTable = this.document.getPicturesTable();
//
//        List<Picture> pictures = picturesTable.getAllPictures();
//        int index = 1;
//        for (Picture picture : pictures) {
////            picture.
//            DocPicture docPicture = new DocPicture();
////            docPicture.setTextBefore();
//            docPicture.setHeight(picture.getHeight());
//            docPicture.setWidth(picture.getWidth());
//            docPicture.setSuggestFileExtension(picture.suggestFileExtension());
//            String base64 = Base64.encodeBase64String(picture.getContent());
//            docPicture.setBase64Content(base64);
//            this.docPictures.add(docPicture);
//
//        }
//    }

    private void processContent() {
        if (null != this.document) {
            try {
                Range range = this.document.getRange();
                int index = 0;
                int number = range.numParagraphs();
                while (number > index) {
                    Paragraph paragraph = range.getParagraph(index);
                    if (paragraph.isInTable()) {
                        int start = index;
                        int end = start + 1;
                        while (true) {
                            if (end >= number) break;
                            paragraph = range.getParagraph(end);
                            if (!paragraph.isInTable()) break;
                            end += 1;
                        }
                        docTables.add(this.processTable(range, start, end));
                        index = end;
                    } else {
                        this.docParagraphs.add(this.processParagraph(paragraph, index));
                        index += 1;
                    }
                }
            } catch (Exception e) {
                // 捕捉到异常，
                e.printStackTrace();
            }
        }
    }

    public List<SuperTable> getAllTables() {
        List<SuperTable> contextList = Lists.newArrayList();
        for (DocTable docTable : this.docTables) {
            contextList.add(docTable);
        }
        return contextList;
    }


    public List<SuperParagraph> getAllParagraphs() {
        List<SuperParagraph> contextList = Lists.newArrayList();
        Map<String, int[]> map = new HashMap<>();
        Map<String, int[]> mapO = new HashMap<>();
        int firstNum = 0;
        int firstNumO = 0;
        for (DocParagraph paragraph : this.docParagraphs) {
            if (paragraph.getLvl() < 9) {
                if(paragraph.getLinfo() == 0){
                    contextList.add(paragraph);
                    continue;
                }else{
                    if(!map.containsKey(paragraph.getLlvl()+""+paragraph.getLinfo())){
                        map.put(paragraph.getLlvl()+""+paragraph.getLinfo(), new int[] {0,0,0,0});
                    }

                    int[] levelCurrentValues = map.get(paragraph.getLlvl()+""+paragraph.getLinfo());
                    String titleName = paragraph.getParagraphText();

                    if(paragraph.getLlvl() == 0){
                        firstNum += 1;
                    }
                    String levelText = firstNum+".";
                    levelCurrentValues[paragraph.getLlvl()] += 1;
                    for(int i = 1; i <= paragraph.getLlvl(); i++){
                        if(levelCurrentValues[i] == 0){
                            levelCurrentValues[i] += 1;
                        }
                        levelText += levelCurrentValues[i] + ".";
                    }
                    paragraph.setParagraphText(levelText + titleName);
                    map.put(paragraph.getLlvl()+""+paragraph.getLinfo(), levelCurrentValues);
                    contextList.add(paragraph);
                }

            }else{
                if (paragraph.getLinfo() > 0){
                    if(!mapO.containsKey(paragraph.getLlvl()+""+paragraph.getLinfo())){
                        mapO.put(paragraph.getLlvl()+""+paragraph.getLinfo(), new int[] {0,0,0,0});
                    }

                    int[] levelCurrentValues = mapO.get(paragraph.getLlvl()+""+paragraph.getLinfo());
                    String titleName = paragraph.getParagraphText();

                    if(paragraph.getLlvl() == 0){
                        firstNumO += 1;
                    }
                    String levelText = firstNumO+".";
                    levelCurrentValues[paragraph.getLlvl()] += 1;
                    for(int i = 1; i <= paragraph.getLlvl(); i++){
                        if(levelCurrentValues[i] == 0){
                            levelCurrentValues[i] += 1;
                        }
                        levelText += levelCurrentValues[i] + ".";
                    }
                    paragraph.setParagraphText(levelText + titleName);
                    mapO.put(paragraph.getLlvl()+""+paragraph.getLinfo(), levelCurrentValues);
                    contextList.add(paragraph);
                }else{
                    contextList.add(paragraph);
                }
            }
        }
        return contextList;
    }

    public List<SuperParagraph> getAllHeads() {
        List<SuperParagraph> headList = Lists.newArrayList();

        Map<String, int[]> map = new HashMap<>();
        Map<String, int[]> mapO = new HashMap<>();
        int firstNum = 0;
        int firstNumO = 0;
        for (DocParagraph paragraph : this.docParagraphs) {
            if (paragraph.getLvl() < 9) {
                if(paragraph.getLinfo() == 0){
                    headList.add(paragraph);
                    continue;
                }else{
                    if(!map.containsKey(paragraph.getLlvl()+""+paragraph.getLinfo())){
                        map.put(paragraph.getLlvl()+""+paragraph.getLinfo(), new int[] {0,0,0,0});
                    }

                    int[] levelCurrentValues = map.get(paragraph.getLlvl()+""+paragraph.getLinfo());
                    String titleName = paragraph.getParagraphText();

                    if(paragraph.getLlvl() == 0){
                        firstNum += 1;
                    }
                    String levelText = firstNum+".";
                    levelCurrentValues[paragraph.getLlvl()] += 1;
                    for(int i = 1; i <= paragraph.getLlvl(); i++){
                        if(levelCurrentValues[i] == 0){
                            levelCurrentValues[i] += 1;
                        }
                        levelText += levelCurrentValues[i] + ".";
                    }
                    paragraph.setParagraphText(levelText + titleName);
                    map.put(paragraph.getLlvl()+""+paragraph.getLinfo(), levelCurrentValues);
                    headList.add(paragraph);
                }

            }else{
                if (paragraph.getLinfo() > 0){
                    if(!mapO.containsKey(paragraph.getLlvl()+""+paragraph.getLinfo())){
                        mapO.put(paragraph.getLlvl()+""+paragraph.getLinfo(), new int[] {0,0,0,0});
                    }

                    int[] levelCurrentValues = mapO.get(paragraph.getLlvl()+""+paragraph.getLinfo());
                    String titleName = paragraph.getParagraphText();

                    if(paragraph.getLlvl() == 0){
                        firstNumO += 1;
                    }
                    String levelText = firstNumO+".";
                    levelCurrentValues[paragraph.getLlvl()] += 1;
                    for(int i = 1; i <= paragraph.getLlvl(); i++){
                        if(levelCurrentValues[i] == 0){
                            levelCurrentValues[i] += 1;
                        }
                        levelText += levelCurrentValues[i] + ".";
                    }
                    paragraph.setParagraphText(levelText + titleName);
                    mapO.put(paragraph.getLlvl()+""+paragraph.getLinfo(), levelCurrentValues);
                    headList.add(paragraph);
                }
            }
        }
        return headList;
    }

    public List<SuperPicture> getAllPictures() {
        List<SuperPicture> superPictureList = Lists.newArrayList();
//        SimplePropertyPreFilter filter = new SimplePropertyPreFilter();
//        filter.getExcludes().add("paragraph");
//        for (DocPicture docParagraph : this.docPictures) {
//            pictureList.add(JSON.toJSONString(docParagraph, filter));
//        }
        for (DocPicture docParagraph : this.docPictures) {
            superPictureList.add(docParagraph);
        }
        return superPictureList;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public InputStream getStream() {
        return stream;
    }

    public void setStream(InputStream stream) {
        this.stream = stream;
    }

    public HWPFDocument getDocument() {
        return document;
    }

    public void setDocument(HWPFDocument document) {
        this.document = document;
    }

    public List<DocParagraph> getDocParagraphs() {
        return docParagraphs;
    }

    public void setDocParagraphs(List<DocParagraph> docParagraphs) {
        this.docParagraphs = docParagraphs;
    }

    public List<DocTable> getDocTables() {
        return docTables;
    }

    public void setDocTables(List<DocTable> docTables) {
        this.docTables = docTables;
    }

    public List<DocPicture> getDocPictures() {
        return docPictures;
    }

    public void setDocPictures(List<DocPicture> docPictures) {
        this.docPictures = docPictures;
    }

    public int getDocTableNum() {
        return docTableNum;
    }

    public void setDocTableNum(int docTableNum) {
        this.docTableNum = docTableNum;
    }
}
