package main.java.com.mooctest.domainObject;

import lombok.Data;
import main.java.com.mooctest.domainObject.DocParser.DocParser;
import main.java.com.mooctest.domainObject.DocxParser.DocxParser;
import main.java.com.mooctest.domainObject.PdfParser.PdfParser;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.List;

@Data
public class WordParser implements Serializable {
    private transient File file;
    private DocParser docParser;
    private DocxParser docxParser;
    private PdfParser pdfParser;
    private String ext = null;
    private String fileType = "";

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public DocParser getDocParser() {
        return docParser;
    }

    public void setDocParser(DocParser docParser) {
        this.docParser = docParser;
    }

    public DocxParser getDocxParser() {
        return docxParser;
    }

    public void setDocxParser(DocxParser docxParser) {
        this.docxParser = docxParser;
    }

    public PdfParser getPdfParser() {
        return pdfParser;
    }

    public void setPdfParser(PdfParser pdfParser) {
        this.pdfParser = pdfParser;
    }

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public WordParser() {}

    public WordParser(File file, DocParser docParser, DocxParser docxParser, PdfParser pdfParser, String ext, String fileType) {
        this.file = file;
        this.docParser = docParser;
        this.docxParser = docxParser;
        this.pdfParser = pdfParser;
        this.ext = ext;
        this.fileType = fileType;
    }

    @Async
    public WordParser parser(MultipartFile uploadFile, String fileName) throws IOException {
        String absolutePath = System.getProperty("user.dir");
        String path = absolutePath + "/fileTemp/";
        File destFile = new File(path, fileName);
        // 将MultipartFile存到临时文件中
        uploadFile.transferTo(destFile);
        BufferedReader bufferedReader = new BufferedReader(new FileReader(destFile));
        bufferedReader.close();

        this.setFile(destFile);
        String fileLowerName = fileName.toLowerCase();
        if (fileLowerName.endsWith(".doc")) {
            try {
                docParser = new DocParser(this.getFile());
                this.setExt(".doc");
                this.setFileType(".doc");
                deleteFile(destFile);
                return this;
            } catch (Exception e) {
                e.printStackTrace();
                deleteFile(destFile);
                return null;
            }
        }
        else if (fileLowerName.endsWith(".docx")) {
            try {
                docxParser = new DocxParser(this.getFile());
                this.setExt(".docx");
                this.setFileType(".docx");
                deleteFile(destFile);
                return this;
            } catch (Exception e) {
                e.printStackTrace();
                deleteFile(destFile);
                return null;
            }
        }
//        else if (fileLowerName.endsWith(".wps")) {
//            try {
//                docParser = new DocParser();
//                this.setExt(".wps");
//                this.setFileType(".doc");
//                return true;
//            } catch (Exception e) {
//                e.printStackTrace();
//                return false;
//            }
//        }
        else if (fileLowerName.endsWith(".pdf")) {
            try {
                pdfParser = new PdfParser(this.getFile());
                this.setExt(".pdf");
                this.setFileType(".pdf");
                deleteFile(destFile);
                return this;
            } catch (Exception e) {
                e.printStackTrace();
                deleteFile(destFile);
                return null;
            }
        }
        return null;
    }

    private void deleteFile(File file){
        // 操作完上的文件 需要删除在根目录下生成的文件
        if (!file.delete()){
            System.out.println("删除失败");
        }
    }

    public List<SuperParagraph> getAllParagraphs() {
        return this.ext == null ? null : this.fileType.equals(".doc") ? this.docParser.getAllParagraphs() : this.fileType.equals(".docx") ?
                this.docxParser.getAllParagraphs() : this.pdfParser.getAllParagraphs();
    }

    public List<SuperTable> getAllTables() {
        return this.ext == null ? null : this.fileType.equals(".doc") ? this.docParser.getAllTables() : this.fileType.equals(".docx") ?
                this.docxParser.getAllTables() : this.pdfParser.getAllTables();
    }

    public List<SuperParagraph> getAllHeads() {
        return this.ext == null ? null : this.fileType.equals(".doc") ? this.docParser.getAllHeads() : this.docxParser.getAllHeads();
    }

    public List<SuperPicture> getAllPictures() {
        return this.ext == null ? null : this.fileType.equals(".doc") ? this.docParser.getAllPictures() : this.fileType.equals(".docx") ?
                this.docxParser.getAllPictures() : this.pdfParser.getAllPictures();
    }
}
