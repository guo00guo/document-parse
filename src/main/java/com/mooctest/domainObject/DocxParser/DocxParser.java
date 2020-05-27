package main.java.com.mooctest.domainObject.DocxParser;

import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;
import com.google.common.collect.Lists;
import com.microsoft.schemas.vml.CTShape;
import main.java.com.mooctest.domainObject.SuperParagraph;
import main.java.com.mooctest.domainObject.SuperPicture;
import main.java.com.mooctest.domainObject.SuperTable;
import lombok.Data;
import main.java.com.mooctest.utils.ReadWordTable;
import org.apache.commons.codec.binary.Base64;
import org.apache.poi.xwpf.usermodel.*;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGraphicalObject;
import org.openxmlformats.schemas.drawingml.x2006.picture.CTPicture;
import org.openxmlformats.schemas.drawingml.x2006.wordprocessingDrawing.CTInline;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.namespace.QName;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Data
public class DocxParser implements Serializable {
    private transient File file;
    private transient InputStream stream = null;
    private transient XWPFDocument document = null;
    private List<DocxParagraph> docxParagraphs = new ArrayList<DocxParagraph>();
    private List<DocxTable> docxTables = new ArrayList<DocxTable>();
    private List<DocxPicture> docxPictures = new ArrayList<DocxPicture>();
    private int paragraph_index = 0;
    private int table_index = 0;
    private int picture_index = 0;
    private transient String pre_type = "段落";
    private transient String asciiFontName = "";
    private transient String eastAsiaFontName = "";
    private List<String> pictureNames = new ArrayList<String>();
    private transient boolean errorWord = false;



    public DocxParser(List<DocxParagraph> docxParagraphs, List<DocxTable> docxTables, List<DocxPicture> docxPictures, int paragraph_index,
                      int table_index, int picture_index, List<String> pictureNames) {
        this.docxParagraphs = docxParagraphs;
        this.docxTables = docxTables;
        this.docxPictures = docxPictures;
        this.paragraph_index = paragraph_index;
        this.table_index = table_index;
        this.picture_index = picture_index;
        this.pictureNames = pictureNames;
    }

    public DocxParser(){}

    public DocxParser(File file) {
        this.file = file;
        this.loadFile();
        if (!errorWord) {
            this.processDefaultValue();
            this.processContent();
        }
    }

    private void processDefaultValue() {
        XWPFDefaultRunStyle xwpfDefaultRunStyle = this.document.getStyles().getDefaultRunStyle();
//        if (xwpfDefaultRunStyle.getRPr().getRFonts().getEastAsia() != null) {
//            this.setEastAsiaFontName(xwpfDefaultRunStyle.getRPr().getRFonts().getEastAsia());
//        } else {
//            this.setEastAsiaFontName("");
//        }
//        if (xwpfDefaultRunStyle.getRPr().getRFonts().getAscii() != null) {
//            this.setAsciiFontName(xwpfDefaultRunStyle.getRPr().getRFonts().getAscii());
//        } else {
//            this.setAsciiFontName("");
//        }

    }

    protected void finalize() {
        if (null != this.document) {
            try {
                this.document.close();//xwpf
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

    private void loadFile() {
        try {
            this.stream = new FileInputStream(this.file);
            this.document = new XWPFDocument(this.stream).getXWPFDocument();
        } catch (Exception e) {
            errorWord = true;
        }
    }


    /**
     * @param str ww
     * @return s
     */
    private static String unescapeJava(String str) {
        String dest = "";
        if (str != null) {
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(str);
            dest = m.replaceAll("");
        }
        return dest;
    }

    private int getLvl(XWPFParagraph paragraph) {
        XWPFStyles styles = this.document.getStyles();
        int lvl = 9;
        try {
            // 判断该段落是否设置了大纲级别
            if (paragraph.getCTP().isSetPPr()) {
                lvl = paragraph.getCTP().getPPr().getOutlineLvl().getVal().intValue();
            }
        } catch (Exception e) {
//            e.printStackTrace();
        }
        try {
            //判断该段落的样式是否设置了大纲级别
            if (paragraph.getStyle() != null) {
                lvl = styles.getStyle(paragraph.getStyle()).getCTStyle().getPPr()
                        .getOutlineLvl().getVal().intValue();
            }

        } catch (Exception e) {
//            e.printStackTrace();
        }
        try {
            //判断该段落的样式的基础样式是否设置了大纲级别
            lvl = styles
                    .getStyle(styles.getStyle(paragraph.getStyle()).getCTStyle().getBasedOn().getVal())
                    .getCTStyle().getPPr().getOutlineLvl().getVal().intValue();
        } catch (Exception e) {
//            e.printStackTrace();
        }
        return lvl;
    }

    private String processText(XWPFParagraph paragraph) {
//        return unescapeJava(paragraph.getParagraphText());
        //xwpf
        if (null != paragraph.getNumLevelText()) {
            return paragraph.getNumLevelText() + paragraph.getNumFmt() + unescapeJava(paragraph.getParagraphText());
        } else {
            return unescapeJava(paragraph.getParagraphText());
        }

    }

    private String getFontFamily(XWPFRun xwpfRun) {
        return xwpfRun.getFontFamily() != null ? xwpfRun.getFontFamily() : this.getEastAsiaFontName();
//        return xwpfRun.getFontFamily() != null ? xwpfRun.getFontFamily() : "";
    }

    private String getColor(XWPFRun xwpfRun) {
        return xwpfRun.getColor() != null ? xwpfRun.getColor() : "000000";
    }

    private void processTable(XWPFTable xwpfTable, int index) {
        DocxTable docxTable = new DocxTable();
        docxTable.setIndex(index);

        //设置表格前面的段落信息
        if (this.paragraph_index > 0) {
            docxTable.setParagraphBefore(this.docxParagraphs.get(this.paragraph_index - 1));
            docxTable.setTextBefore(this.docxParagraphs.get(this.paragraph_index - 1).getParagraphText());
        }

        ReadWordTable readWordTable = new ReadWordTable();

        // 处理合并单元格
        int tableRowsSize = xwpfTable.getRows().size();
        // 获取每一行
        for (int rowIndex = 0; rowIndex < tableRowsSize; rowIndex++) {
            int tableCellsSize = xwpfTable.getRow(rowIndex).getTableCells().size();
            List<List<DocxParagraph>> xtableRow = new ArrayList<>();

            // 获取每一列
            for (int colIndex = 0; colIndex < tableCellsSize; colIndex++) {
                if (readWordTable.isOmitCell(rowIndex, colIndex)) {
                    continue;
                }

                XWPFTableCell xwpfTableCell = xwpfTable.getRow(rowIndex).getCell(colIndex);
                // 获取单元格的属性
                CTTcPr tcPr = xwpfTableCell.getCTTc().getTcPr();
                int colspan = readWordTable.getColspan(tcPr);
                int rowspan = readWordTable.getRowspan(xwpfTable, rowIndex, colIndex);

                List<DocxParagraph> paragraphsCell = new ArrayList<>();
                for (XWPFParagraph xwpfParagraph : xwpfTableCell.getParagraphs()) {
                    // 处理单元格中的段落信息
                    DocxParagraph docxParagraph = this.processParagraph(xwpfParagraph, this.paragraph_index++);
                    docxParagraph.setInTable(true);
                    // 当存在合并的单元格时，获取合并的行列数
                    if (colspan > 1) { // 合并的列
                        docxParagraph.setColspan(colspan);
                    }
                    if (rowspan > 1) { // 合并的行
                        docxParagraph.setRowspan(rowspan);
                    }
                    paragraphsCell.add(docxParagraph);

                }
                xtableRow.add(paragraphsCell);
            }

            docxTable.setAlignment(xwpfTable.getTableAlignment());
            docxTable.docxTableContent.add(xtableRow);
        }

        //设置表格后面的段落信息
        if (this.paragraph_index <= this.docxParagraphs.size()) {
            docxTable.setParagraphAfter(this.docxParagraphs.get(this.paragraph_index-1));
            docxTable.setTextAfter(this.docxParagraphs.get(this.paragraph_index-1).getParagraphText());
        }
        this.docxTables.add(docxTable);
    }

    private DocxParagraph processParagraph(XWPFParagraph paragraph, int index) {
        //解析段落信息
        DocxParagraph docxParagraph = new DocxParagraph();
        docxParagraph.setFirstLineIndent(paragraph.getFirstLineIndent());
        docxParagraph.setFontAlignment(paragraph.getAlignment());
        docxParagraph.setIndentFromLeft(paragraph.getIndentFromLeft());
        docxParagraph.setIndentFromRight(paragraph.getIndentFromRight());
        docxParagraph.setLvl(this.getLvl(paragraph));
        docxParagraph.setParagraphText(this.processText(paragraph));
        docxParagraph.setLineSpacing(paragraph.getSpacingLineRule().getValue());
        docxParagraph.setParagraphID(index);
        docxParagraph.setAlignment(paragraph.getAlignment());
        docxParagraph.setNumFmt(paragraph.getNumFmt());
        docxParagraph.setNumIlvl(paragraph.getNumIlvl());
        docxParagraph.setNumLevelText(paragraph.getNumLevelText());
        docxParagraph.setNumId(paragraph.getNumID());

        //解析字体格式等
        String fontName = "";
        String color = "";
        int fontSize = -1;
        boolean bold = false;
        boolean italic = false;
        boolean highlighted = false;
        boolean strike = false;
        UnderlinePatterns underline = UnderlinePatterns.NONE;
        List<XWPFRun> xwpfRuns = paragraph.getRuns();
        for (int i = 0; i < xwpfRuns.size(); i++) {
            XWPFRun xwpfRun = xwpfRuns.get(i);
            if (i == 0) {
                fontSize = xwpfRun.getFontSize();
                fontName = this.getFontFamily(xwpfRun); // getFontFamily() ，仅当运行的run属性中存在字体系列时，它才会返回字体系列。否则它将返回null
                color = this.getColor(xwpfRun);
                bold = xwpfRun.isBold();
                italic = xwpfRun.isItalic();
                //xwpf
                highlighted = xwpfRun.isHighlighted();
                strike = xwpfRun.isStrikeThrough();
                underline = xwpfRun.getUnderline();
//                strike = xwpfRun.isStrike();
            } else {
                if (fontSize == -1 && fontName.equals("") && color.equals("")) break;
                if (fontSize != xwpfRun.getFontSize()) fontSize = -1;
                if (!fontName.equals(this.getFontFamily(xwpfRun))) fontName = "";
                if (!color.equals(this.getColor(xwpfRun))) color = "";
                if (bold != xwpfRun.isBold()) bold = false;
                if (italic != xwpfRun.isItalic()) italic = false;
                if (highlighted != xwpfRun.isHighlighted()) highlighted = false;
                if (strike != xwpfRun.isStrikeThrough()) strike = false;
                if (strike != xwpfRun.isStrike()) strike = false;
                if (underline != xwpfRun.getUnderline()) underline = UnderlinePatterns.NONE;

            }
//            if(index <  100){
//                System.out.println(index + "  " + i + "  " + xwpfRun.getText(0) + "  fontSize:"+ fontSize + "  fontName:"+ fontName + "  bold:" + bold);
//            }
        }
        docxParagraph.setFontName(fontName);
        docxParagraph.setFontSize(fontSize);
        docxParagraph.setColor(color);
        docxParagraph.setBold(bold);
        docxParagraph.setItalic(italic);
        docxParagraph.setHighlighted(highlighted);
        docxParagraph.setUnderline(underline);
        docxParagraph.setStrike(strike);

        this.docxParagraphs.add(docxParagraph);

        for (XWPFRun xwpfRun : paragraph.getRuns()) {
             //解析文本框内容，有一些文字是在文本框之内的，需要将文本框内的文字作为段落进一步解析。
            procesGroup(paragraph, xwpfRun);

            //以下是对段落中的图片进行直接解析。
            CTR ctr = xwpfRun.getCTR();
            XmlCursor c = ctr.newCursor();
            //这个就是拿到所有的子元素：
            c.selectPath("./*");
            while (c.toNextSelection()) {
                XmlObject o = c.getObject();
                //如果子元素是<w:drawing>这样的形式，使用CTDrawing保存图片
                if (o instanceof CTDrawing) { // 一般的图片信息，png，jpg等
                    CTDrawing drawing = (CTDrawing) o;
                    CTInline[] ctInlines = drawing.getInlineArray();
                    for (CTInline ctInline : ctInlines) {
                        CTGraphicalObject graphic = ctInline.getGraphic();
                        XmlCursor cursor = graphic.getGraphicData().newCursor();
                        cursor.selectPath("./*");
                        while (cursor.toNextSelection()) {
                            XmlObject xmlObject = cursor.getObject();
                            //如果子元素是<pic:pic>这样的形式
                            if (xmlObject instanceof CTPicture) {
                                CTPicture picture = (CTPicture) xmlObject;
                                //拿到元素的属性
                                this.processPicture(this.document.getPictureDataByID(picture.getBlipFill().getBlip().getEmbed()), this.paragraph_index);
                            }
                        }
                    }
                }
                if (o instanceof CTObject) { // visio图片等，emf
                    CTObject object = (CTObject) o;
                    XmlCursor w = object.newCursor();
                    w.selectPath("./*");
                    while (w.toNextSelection()) {
                        XmlObject xmlObject = w.getObject();
                        if (xmlObject instanceof CTShape) {
                            CTShape myshape = (CTShape) xmlObject;
                            Node node = myshape.getDomNode();
                            Node imageDataNode = getChildNode(node, "v:imagedata");
                            if (imageDataNode != null) {
                                Node blipNode = imageDataNode.getAttributes().getNamedItem("r:id");
                                if (blipNode != null) {
                                    this.processPicture(this.document.getPictureDataByID(blipNode.getNodeValue()), this.paragraph_index);
                                }
                            }
                        }
                    }
                }
            }
        }
        // 下面是找到所有的段落信息
        return docxParagraph;
    }

    private void procesGroup(XWPFParagraph paragraph, XWPFRun xwpfRun) {
        // 首先查看是否存在group的情况，如果存在，则表示存在将两幅图片、图片和文本框进行融合的情况。
        XmlObject[] groupObjects = xwpfRun.getCTR().selectPath("" +
                "declare namespace w='http://schemas.openxmlformats.org/wordprocessingml/2006/main' " +
                "declare namespace wps='http://schemas.microsoft.com/office/word/2010/wordprocessingShape'" +
                "declare namespace v='urn:schemas-microsoft-com:vml' .//*/v:group");
        if (groupObjects.length > 0) { // 存在word中的组合，主要解决有一些文档将图片进行组合的情景
            for (XmlObject groupObject : groupObjects) {
                XmlCursor c = groupObject.newCursor();
                c.selectPath("./*");
                while (c.toNextSelection()) {
                    XmlObject curXmlObject = c.getObject(); //获取子元素
                    // 对图片进行处理，如果存在图片的话，则这个元组含有v:imagedata节点，
                    // 并且该节点必然含有r:id属性值。
                    Node imageDataNode = getChildNode(curXmlObject.getDomNode(), "v:imagedata");
                    if (imageDataNode != null) {
                        Node blipNode = imageDataNode.getAttributes().getNamedItem("r:id");
                        if (blipNode != null) {
                            this.processPicture(this.document.getPictureDataByID(blipNode.getNodeValue()), this.paragraph_index);
                        }
                    }
                    //解析是否存在文本框；
                    processTextBox(paragraph, curXmlObject);
                }
            }
        }
        else { // 查看是否存在文本框的情况。
            XmlObject[] textBoxObjects = xwpfRun.getCTR().selectPath("" +
                    "declare namespace w='http://schemas.openxmlformats.org/wordprocessingml/2006/main' " +
                    "declare namespace wps='http://schemas.microsoft.com/office/word/2010/wordprocessingShape'" +
                    "  .//*/w:textbox");
            for (XmlObject textBoxObject : textBoxObjects) {
                processTextBox(paragraph, textBoxObject);
            }
        }
    }
    private void processTextBox(XWPFParagraph paragraph, XmlObject curObject){
        XmlObject[] textBoxObjects = curObject.selectPath("" +
                "declare namespace w='http://schemas.openxmlformats.org/wordprocessingml/2006/main' " +
                "declare namespace wps='http://schemas.microsoft.com/office/word/2010/wordprocessingShape'" +
                "  .//*/w:txbxContent");
        for (XmlObject textBoxObject : textBoxObjects) {
            try {
                XmlObject[] paraObjects = textBoxObject.
                        selectChildren(
                                new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "p"));
                XWPFParagraph embeddedPara;
                for (XmlObject paraObject : paraObjects) {
                    embeddedPara = new XWPFParagraph(CTP.Factory.parse(paraObject.xmlText()), paragraph.getBody());
                    this.processParagraph(embeddedPara, this.paragraph_index++);
                }
            } catch (Exception e) {
            }
        }
    }

    private Node getChildNode(Node node, String nodeName) {
        if (!node.hasChildNodes()) {
            return null;
        }
        NodeList childNodes = node.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node childNode = childNodes.item(i);
            if (nodeName.equals(childNode.getNodeName())) {
                return childNode;
            }
            childNode = getChildNode(childNode, nodeName);
            if (childNode != null) {
                return childNode;
            }
        }
        return null;
    }

    private void processPicture(XWPFPictureData pictureData, int paragraphID) {
        if (this.pictureNames.contains(pictureData.toString())) return;

        DocxPicture docxPicture = new DocxPicture();
        docxPicture.setParagraphID(paragraphID);
        docxPicture.setIndex(this.picture_index++);
        docxPicture.setFileName(pictureData.getFileName());
        docxPicture.setSuggestFileExtension(pictureData.suggestFileExtension());
        docxPicture.setBase64Content(Base64.encodeBase64String(pictureData.getData()));
        this.setPre_type("图片");
        // 处理图片前的段落信息
        if (paragraphID > 1) {
            docxPicture.setParagraphBefore(this.docxParagraphs.get(paragraphID - 2));
            docxPicture.setTextBefore(this.docxParagraphs.get(paragraphID - 2).getParagraphText());
        }

        // 处理图片后的段落信息
        if (this.paragraph_index <= this.docxParagraphs.size()) {
            docxPicture.setParagraphAfter(this.docxParagraphs.get(this.paragraph_index - 1));
            docxPicture.setTextAfter(this.docxParagraphs.get(this.paragraph_index - 1).getParagraphText());
        }
        this.pictureNames.add(pictureData.toString());
        this.docxPictures.add(docxPicture);
    }

    private void processContent() {
        if (null != this.document) {
            try {
                List<IBodyElement> iBodyElements = document.getBodyElements();
                for (IBodyElement elem : iBodyElements) {
                    // 处理段落
                    if (elem instanceof XWPFParagraph) {
                        this.processParagraph((XWPFParagraph) elem, this.paragraph_index++);
                    }
                    // 处理表格
                    else if (elem instanceof XWPFTable) {
                        this.processTable((XWPFTable) elem, this.table_index++);
                    }
                    // 处理其他
                    else {
                        System.out.println("other");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public List<SuperParagraph> getAllParagraphs() {
        List<SuperParagraph> contextList = Lists.newArrayList();
//        SimplePropertyPreFilter filter = new SimplePropertyPreFilter();
//        filter.getExcludes().add("XWPFParagraph");
        for (DocxParagraph docParagraph : this.docxParagraphs) {
//            System.out.println(docParagraph.toString());
            contextList.add(docParagraph);
        }
        return contextList;
    }

    public List<SuperParagraph> getAllHeads() {
        List<SuperParagraph> contextList = Lists.newArrayList();
//        SimplePropertyPreFilter filter = new SimplePropertyPreFilter();
//        filter.getExcludes().add("XWPFParagraph");
        int[] levelCurrentValues = new int[] {0,0,0,0};
        for (DocxParagraph paragraph : this.docxParagraphs) {
            if (paragraph.getLvl() < 9) {
                contextList.add(paragraph);
                continue;
            }
            //xwpf
            if(paragraph.getNumFmt() != null){
                // 获取标题名称
                String paragraphText = paragraph.getParagraphText();
                String titleName = "";
                if(paragraphText.contains("decimal")){
                    int index = paragraphText.lastIndexOf("decimal");
                    titleName = paragraphText.substring(index+7, paragraphText.length());
                }

                // 获取标题编号
                BigInteger levelDepth = paragraph.getNumIlvl();
                String levelText = paragraph.getNumLevelText();
                if(levelText!=null) {
                    levelCurrentValues[levelDepth.intValue()] += 1;
                    levelText = levelText.replace("%1", "" + levelCurrentValues[0]);
                    levelText = levelText.replace("%2", "" + levelCurrentValues[1]);
                    levelText = levelText.replace("%3", "" + levelCurrentValues[2]);
                    levelText = levelText.replace("%4", "" + levelCurrentValues[3]);
                    paragraph.setParagraphText(levelText + titleName);
                }
                contextList.add(paragraph);
                continue;
            }
//            if(paragraph.getNumId() != null){
//                contextList.add(paragraph);
//            }
        }
        return contextList;
    }

    public List<SuperTable> getAllTables() {
        List<SuperTable> contextList = Lists.newArrayList();
        for (DocxTable docxTable : this.docxTables) {
            //设置表格后面的段落信息
            if (docxTable.getParagraphAfter().getParagraphID() + 1 < this.docxParagraphs.size()) {
                int pre_index = docxTable.getParagraphAfter().getParagraphID();
                docxTable.setParagraphAfter(this.docxParagraphs.get(pre_index + 1));
                docxTable.setTextAfter(this.docxParagraphs.get(pre_index + 1).getParagraphText());
            }
            contextList.add(docxTable);
        }
        return contextList;
    }

    public List<SuperPicture> getAllPictures() {
        List<SuperPicture> contextList = Lists.newArrayList();
        SimplePropertyPreFilter filter = new SimplePropertyPreFilter();
//        filter.getExcludes().add("XWPFParagraph");
        for (DocxPicture docxPicture : this.docxPictures) {
            //设置图片后面的段落信息
            if (docxPicture.getParagraphAfter().getParagraphID()+1 < this.docxParagraphs.size()) {
                int pre_index = docxPicture.getParagraphAfter().getParagraphID();
                docxPicture.setParagraphAfter(this.docxParagraphs.get(pre_index+1));
                docxPicture.setTextAfter(this.docxParagraphs.get(pre_index+1).getParagraphText());
            }
            contextList.add(docxPicture);
        }
        return contextList;
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

    public XWPFDocument getDocument() {
        return document;
    }

    public void setDocument(XWPFDocument document) {
        this.document = document;
    }

    public List<DocxParagraph> getDocxParagraphs() {
        return docxParagraphs;
    }

    public void setDocxParagraphs(List<DocxParagraph> docxParagraphs) {
        this.docxParagraphs = docxParagraphs;
    }

    public List<DocxTable> getDocxTables() {
        return docxTables;
    }

    public void setDocxTables(List<DocxTable> docxTables) {
        this.docxTables = docxTables;
    }

    public List<DocxPicture> getDocxPictures() {
        return docxPictures;
    }

    public void setDocxPictures(List<DocxPicture> docxPictures) {
        this.docxPictures = docxPictures;
    }

    public int getParagraph_index() {
        return paragraph_index;
    }

    public void setParagraph_index(int paragraph_index) {
        this.paragraph_index = paragraph_index;
    }

    public int getTable_index() {
        return table_index;
    }

    public void setTable_index(int table_index) {
        this.table_index = table_index;
    }

    public int getPicture_index() {
        return picture_index;
    }

    public void setPicture_index(int picture_index) {
        this.picture_index = picture_index;
    }

    public String getPre_type() {
        return pre_type;
    }

    public void setPre_type(String pre_type) {
        this.pre_type = pre_type;
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

    public List<String> getPictureNames() {
        return pictureNames;
    }

    public void setPictureNames(List<String> pictureNames) {
        this.pictureNames = pictureNames;
    }

    public boolean isErrorWord() {
        return errorWord;
    }

    public void setErrorWord(boolean errorWord) {
        this.errorWord = errorWord;
    }
}
