package com.mooctest.domainObject.PdfParser;


import com.google.common.collect.Lists;
import com.mooctest.domainObject.SuperParagraph;
import com.mooctest.domainObject.SuperPicture;
import com.mooctest.domainObject.SuperTable;
import lombok.Data;
import org.apache.commons.codec.binary.Base64;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDDocumentOutline;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDOutlineItem;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Data
class MyPDOutlineItem implements Serializable {
    private PDOutlineItem pdOutlineItem;
    private int level;
    private String title;
    private boolean visited = false;

    MyPDOutlineItem(PDOutlineItem pdOutlineItem, int level) {
        this.pdOutlineItem = pdOutlineItem;
        this.level = level;
        this.title = pdOutlineItem.getTitle().trim().replaceAll(" ","");
    }

    MyPDOutlineItem(){}

    public PDOutlineItem getPdOutlineItem() {
        return pdOutlineItem;
    }

    public void setPdOutlineItem(PDOutlineItem pdOutlineItem) {
        this.pdOutlineItem = pdOutlineItem;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isVisited() {
        return visited;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }
}

@Data
public class PdfParser implements Serializable {
    private transient File file;
    private transient InputStream stream = null;
    private transient PDDocument document = null;

    private List<PdfParagraph> pdfParagraphs = new ArrayList<PdfParagraph>();
    private List<PdfPicture> pdfPictures = new ArrayList<PdfPicture>();
    private List<PdfTable> pdfTables = new ArrayList<PdfTable>();
    private int paragraphIndex = 0;
    private transient List<MyPDOutlineItem> pdOutlineItemList = new ArrayList<>();
    private int picture_Index = 0;
    private int curTitleIndex = 0;

    public PdfParser() {}

    public PdfParser(File file, InputStream stream, PDDocument document, List<PdfParagraph> pdfParagraphs, List<PdfPicture> pdfPictures, List<PdfTable> pdfTables, int paragraphIndex, List<MyPDOutlineItem> pdOutlineItemList, int picture_Index, int curTitleIndex) {
        this.file = file;
        this.stream = stream;
        this.document = document;
        this.pdfParagraphs = pdfParagraphs;
        this.pdfPictures = pdfPictures;
        this.pdfTables = pdfTables;
        this.paragraphIndex = paragraphIndex;
        this.pdOutlineItemList = pdOutlineItemList;
        this.picture_Index = picture_Index;
        this.curTitleIndex = curTitleIndex;
    }

    public PdfParser(File file) {
        this.file = file;
        this.loadFile();
        this.getPDFOutline();
        this.processContent();
        this.processPicture();
    }

    protected void finalize() {
        if (null != this.document) {
            try {
                this.document.close();
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

    // todo 读文件
    private void loadFile() {
        try {
            if (this.file.getName().endsWith(".pdf")) {
                document = PDDocument.load(this.file);
            }
        } catch (Exception e) {
            System.out.println("文件加载错误！");
        }
    }


    // todo 将文件中段落中的特殊字符去掉
    private static String unescapeJava(String str) {
        String dest = "";
        if (str != null) {
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(str);
            dest = m.replaceAll("");
        }
        return dest;
    }


    // todo 文本内容
    private String processText(XWPFParagraph paragraph) {
        return unescapeJava(paragraph.getParagraphText());
    }


    //todo 将图片解析出来
    private void processPicture() {
        if (null != document) {
            try {
                int pageSize = document.getNumberOfPages();
                System.out.println(pageSize);
                for (int i = 0; i < pageSize; i++) {
                    // 图片内容
                    PDPage page = document.getPage(i);
                    PDResources resources = page.getResources();
                    Iterable<COSName> cosNames = resources.getXObjectNames();
                    if (cosNames != null) {
                        Iterator<COSName> cosNamesIter = cosNames.iterator();
                        while (cosNamesIter.hasNext()) {
                            COSName cosName = cosNamesIter.next();
                            if (resources.isImageXObject(cosName)) {
                                PDImageXObject pdImage = (PDImageXObject) resources.getXObject(cosName);
                                BufferedImage image = pdImage.getImage();
                                ByteArrayOutputStream outbyte = new ByteArrayOutputStream();
                                ImageIO.write(image, "png", outbyte);
                                byte[] b = outbyte.toByteArray();
                                int size = b.length / 1024;
                                PdfPicture pdfPicture = new PdfPicture();
                                pdfPicture.setBase64Content(Base64.encodeBase64String(b));
                                pdfPicture.setWidth(image.getWidth());
                                pdfPicture.setHeight(image.getHeight());
                                pdfPicture.setSize(size);
                                pdfPicture.setIndex(picture_Index);
                                picture_Index++;
                                this.pdfPictures.add(pdfPicture);
                            }
                        }
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    //todo 解析pdf中文字内容
    private void processContent() {
        if (null != this.document) {
            try {
                int pageSize = document.getNumberOfPages();

                PDFTextStripper stripper = new PDFTextStripper() {
                    float minStartPos = 500;
                    float maxEndPos = 0;
                    float lastLineFirstPos;
                    float lastLineLastPos;
                    boolean isParagraphStart = true;
                    boolean startOfLine = true;
                    float curHeight = -1;

                    @Override
                    protected void startPage(PDPage page) throws IOException {
                        startOfLine = true;
                        boolean isParagraphStart = true;
                        super.startPage(page);
                    }

                    @Override
                    protected void writeLineSeparator() throws IOException {
                        startOfLine = true;
                        super.writeLineSeparator();
                    }

                    @Override
                    protected void writeString(String text, List<TextPosition> textPositions) throws IOException {
                        if (startOfLine) {
                            TextPosition firstPosition = textPositions.get(0);
                            TextPosition lastPosition = textPositions.get(textPositions.size() - 1);
                            if (isParagraphStart) {
                                super.writeString(text, textPositions);
                                minStartPos = Math.min(minStartPos, firstPosition.getXDirAdj());
                                maxEndPos = Math.max(maxEndPos, lastPosition.getXDirAdj());
                                curHeight = firstPosition.getYDirAdj();
                                lastLineFirstPos = firstPosition.getXDirAdj();
                                lastLineLastPos = lastPosition.getXDirAdj();
                                isParagraphStart = false;
                                return;
                            } else if (Math.abs(curHeight - firstPosition.getYDirAdj()) < 10) {
                                super.writeString(text, textPositions);
                                minStartPos = Math.min(minStartPos, firstPosition.getXDirAdj());
                                maxEndPos = Math.max(maxEndPos, lastPosition.getXDirAdj());
                                lastLineLastPos = lastPosition.getXDirAdj();
                                return;
                            } else {
                                if (curHeight < (firstPosition.getXDirAdj() - (1.5 * firstPosition.getHeight())) || firstPosition.getXDirAdj() > lastLineFirstPos + 10 || lastPosition.getXDirAdj() > lastLineLastPos + 10) {
                                    writeString("\r\n");
                                    super.writeString(text, textPositions);
                                    curHeight = firstPosition.getYDirAdj();
                                    minStartPos = Math.min(minStartPos, firstPosition.getXDirAdj());
                                    maxEndPos = Math.max(maxEndPos, lastPosition.getXDirAdj());
                                    lastLineLastPos = lastPosition.getXDirAdj();
                                    lastLineFirstPos = firstPosition.getXDirAdj();
                                    return;
                                }
                                if (lastPosition.getXDirAdj() < lastLineLastPos - 10) {//末尾
                                    super.writeString(text, textPositions);
                                    curHeight = firstPosition.getYDirAdj();
                                    minStartPos = Math.min(minStartPos, firstPosition.getXDirAdj());
                                    maxEndPos = Math.max(maxEndPos, lastPosition.getXDirAdj());
                                    lastLineLastPos = lastPosition.getXDirAdj();
                                    lastLineFirstPos = firstPosition.getXDirAdj();
                                    return;
                                }
                                if (Math.abs(firstPosition.getXDirAdj() - lastLineFirstPos) < 5 || Math.abs(lastPosition.getXDirAdj() - lastLineLastPos) < 5) { //中间
                                    super.writeString(text, textPositions);
                                    curHeight = firstPosition.getYDirAdj();
                                    minStartPos = Math.min(minStartPos, firstPosition.getXDirAdj());
                                    maxEndPos = Math.max(maxEndPos, lastPosition.getXDirAdj());
                                    lastLineLastPos = lastPosition.getXDirAdj();
                                    lastLineFirstPos = firstPosition.getXDirAdj();
                                    return;
                                }
                            }
                            startOfLine = false;
                        }
                        super.writeString(text, textPositions);
                    }
                };
                for (int i = 0; i < pageSize; i++) {
                    //文本内容
                    // 设置按顺序输出
                    stripper.setSortByPosition(true);
                    stripper.setWordSeparator(new String(""));
                    stripper.setLineSeparator(new String(""));
                    stripper.setParagraphEnd(new String(""));
                    stripper.setParagraphStart(new String(""));
                    stripper.setPageEnd(new String(""));
                    stripper.setStartPage(i + 1);
                    stripper.setEndPage(i + 1);
                    String text = stripper.getText(document);
                    stripper.getParagraphStart();
                    wordToParagraph(text);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void getChildOutLine(PDOutlineItem item, int level) {
        if (item == null) return;
        System.out.println("level：" + level + "->" + item.getTitle());
        MyPDOutlineItem myPDOutlineItem = new MyPDOutlineItem(item, level);
        pdOutlineItemList.add(myPDOutlineItem);
        PDOutlineItem child = item.getFirstChild();
        while (child != null) {
            this.getChildOutLine(child, level + 1);
            child = child.getNextSibling();
        }
    }

    public void getPDFOutline() {
        //获取PDDocumentCatalog文档目录对象
        PDDocumentCatalog catalog = this.document.getDocumentCatalog();
        //获取PDDocumentOutline文档纲要对象
        PDDocumentOutline outline = catalog.getDocumentOutline();
        int level = 1;
        if (outline != null) {
            //获取第一个纲要条目（标题1）
            PDOutlineItem item = outline.getFirstChild();
            //遍历每一个标题1
            while (item != null) {
                this.getChildOutLine(item, level);
                //指向下一个标题1
                item = item.getNextSibling();
            }
        }
    }

    private int getParagraphLevel(String string) {
        string = string.replaceAll(" ", "");
        for (MyPDOutlineItem myPDOutlineItem : this.pdOutlineItemList) {
            if (!myPDOutlineItem.isVisited()) {
                if (myPDOutlineItem.getTitle().equals(string)) {
                    myPDOutlineItem.setVisited(true);
                    System.out.println(myPDOutlineItem.getLevel());
                    return myPDOutlineItem.getLevel();
                }
            }
        }
        return 9;
    }

    //todo 将文字转化为段落
    private void wordToParagraph(String text) {
        if (text != null) {
            String[] splittext = text.split("\r\n");
            for (String string : splittext) {
                PdfParagraph pdfParagraph = new PdfParagraph();
                pdfParagraph.setParagraphText(string);
                if (string.length() < 20) {
                    pdfParagraph.setLevel(this.getParagraphLevel(string));
                }
                pdfParagraph.setParagraphID(paragraphIndex);
                paragraphIndex++;
                this.pdfParagraphs.add(pdfParagraph);
            }
        }
    }

    // 获取所有段落文字
    public List<SuperParagraph> getAllParagraphs() {
        List<SuperParagraph> contextList = Lists.newArrayList();
        for (PdfParagraph pdfParagraph : this.pdfParagraphs) {
            contextList.add(pdfParagraph);
        }
        return contextList;
    }

    // 获取所有图片
    public List<SuperPicture> getAllPictures() {
        List<SuperPicture> superPictureList = Lists.newArrayList();
        for (PdfPicture pdfPicture : this.pdfPictures) {
            superPictureList.add(pdfPicture);
        }
        return superPictureList;
    }

    // 获取所有表
    public List<SuperTable> getAllTables() {
        List<SuperTable> contextList = Lists.newArrayList();
        for (PdfTable docxTable : this.pdfTables) {
            contextList.add(docxTable);
        }
        return contextList;
    }

    public List<SuperParagraph> getAllHeads(){
        List<SuperParagraph> contextList = Lists.newArrayList();
        for (PdfParagraph pdfParagraph : this.pdfParagraphs) {
            contextList.add(pdfParagraph);
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

    public PDDocument getDocument() {
        return document;
    }

    public void setDocument(PDDocument document) {
        this.document = document;
    }

    public List<PdfParagraph> getPdfParagraphs() {
        return pdfParagraphs;
    }

    public void setPdfParagraphs(List<PdfParagraph> pdfParagraphs) {
        this.pdfParagraphs = pdfParagraphs;
    }

    public List<PdfPicture> getPdfPictures() {
        return pdfPictures;
    }

    public void setPdfPictures(List<PdfPicture> pdfPictures) {
        this.pdfPictures = pdfPictures;
    }

    public List<PdfTable> getPdfTables() {
        return pdfTables;
    }

    public void setPdfTables(List<PdfTable> pdfTables) {
        this.pdfTables = pdfTables;
    }

    public int getParagraphIndex() {
        return paragraphIndex;
    }

    public void setParagraphIndex(int paragraphIndex) {
        this.paragraphIndex = paragraphIndex;
    }

    public List<MyPDOutlineItem> getPdOutlineItemList() {
        return pdOutlineItemList;
    }

    public void setPdOutlineItemList(List<MyPDOutlineItem> pdOutlineItemList) {
        this.pdOutlineItemList = pdOutlineItemList;
    }

    public int getPicture_Index() {
        return picture_Index;
    }

    public void setPicture_Index(int picture_Index) {
        this.picture_Index = picture_Index;
    }

    public int getCurTitleIndex() {
        return curTitleIndex;
    }

    public void setCurTitleIndex(int curTitleIndex) {
        this.curTitleIndex = curTitleIndex;
    }
}
