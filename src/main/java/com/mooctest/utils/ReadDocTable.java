package main.java.com.mooctest.utils;//package com.mooctest.utils;
//
//import org.apache.pdfbox.pdmodel.PDDocument;
//import org.apache.pdfbox.text.PDFTextStripper;
//
//import java.io.*;
//
///**
// * @author guochao
// * @date 2020-05-13 09:38
// */
//public class ReadDocTable {
//    public static void main(String[] args) throws Exception {
//        ParsePdf("/Users/guochao/Desktop/文档解析模块V0.3.pdf");
//        String oldData = parseTxt(new File("/Users/guochao/Desktop/文档解析模块V0.3.txt"));
//        System.out.println(oldData);
//    }
//
//    public static void ParsePdf(String path) throws Exception {
//        File file = new File(path);
//        PDDocument doc = PDDocument.load(file);
//        int pagenumber = doc.getNumberOfPages();
//        // System.out.print("pages" + pagenumber);
//        String s = path.substring(0, path.length() - 3);// 这边截取的时候 写死了 所以文件只能放在类似 D：//下面
//        FileOutputStream fos = new FileOutputStream(s + "txt");
//        Writer writer = new OutputStreamWriter(fos, "UTF-8");
//        PDFTextStripper stripper = new PDFTextStripper();
//        stripper.setSortByPosition(true);// 排序
//        stripper.writeText(doc, writer);
//        writer.close();
//        doc.close();
//    }
//
//    public static String parseTxt(File file) {
//        StringBuilder result = new StringBuilder();
//        try {
//            BufferedReader br = new BufferedReader(new FileReader(file));// 构造一个BufferedReader类来读取文件
//            String s = null;
//            int lineIndex = 0;
//            while ((s = br.readLine()) != null) {// 使用readLine方法，一次读一行
//                lineIndex++;
//                String regex = "^[0-9].*[0-9]$";//从头到尾之后数字，来作为判断表格的依据
//                if (s.matches(regex)) {
//                    System.out.println(lineIndex + ":" + s);
//                    result.append(System.lineSeparator() + s);
//                }
//            }
//            br.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return result.toString();
//    }
//}
