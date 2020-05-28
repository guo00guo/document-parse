package com.mooctest.service;

import com.google.gson.Gson;
import com.mooctest.data.StyleWrapper;
import com.mooctest.domainObject.DocParser.DocParagraph;
import com.mooctest.domainObject.DocParser.DocParser;
import com.mooctest.domainObject.DocParser.DocPicture;
import com.mooctest.domainObject.DocParser.DocTable;
import com.mooctest.domainObject.DocxParser.DocxParagraph;
import com.mooctest.domainObject.DocxParser.DocxParser;
import com.mooctest.domainObject.DocxParser.DocxPicture;
import com.mooctest.domainObject.DocxParser.DocxTable;
import com.mooctest.domainObject.PdfParser.PdfParagraph;
import com.mooctest.domainObject.PdfParser.PdfParser;
import com.mooctest.domainObject.PdfParser.PdfPicture;
import com.mooctest.domainObject.PdfParser.PdfTable;
import com.mooctest.domainObject.*;
import lombok.extern.slf4j.Slf4j;
import com.mooctest.exception.HttpBadRequestException;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author guochao
 * @date 2020-05-08 17:30
 */
@Service
@Slf4j
public class ParserServiceImpl implements ParserService {

    @Autowired
    private RedisTemplate<Object,Object> redisTemplate;
    @Autowired
    private ParserAsyncImpl parserAsync;
    @Value("${redis.timeout}")
    private int EXP_TIMES;

    @Override
    public String parserFile(MultipartFile uploadFile) throws IOException {
        String token = parseFileContent(uploadFile);
        return token;
    }

    @Override
    public List<SuperParagraph> getAllPara(String token){
//        checkToken(token);
        WordParser wordParser = getResultBean(token);
        List<SuperParagraph> paragraphs = wordParser.getAllParagraphs();
        return paragraphs;
    }

    private void checkToken(String token) {
        ValueOperations valueOperations = redisTemplate.opsForValue();
        Object redisContent = valueOperations.get(token);
        Object redisContentTemplate = valueOperations.get(token + "-" + EXP_TIMES);
        if(redisContent == null && redisContentTemplate == null){
            throw new HttpBadRequestException("token已失效！");
        }else if(redisContent == null && redisContentTemplate != null){
            throw new HttpBadRequestException("正在解析中！");
        }
    }

    @Override
    public List<SuperPicture> getAllPicture(String token){
        WordParser wordParser = getResultBean(token);
        List<SuperPicture> allSuperPictures = wordParser.getAllPictures();
        return allSuperPictures;
    }

    @Override
    public List<SuperTable> getAllTable(String token){
        WordParser wordParser = getResultBean(token);
        List<SuperTable> allSuperTables = wordParser.getAllTables();
        return allSuperTables;
    }

    @Override
    public List<SuperParagraph> getAllTitle(String token){
        WordParser wordParser = getResultBean(token);
        if(token.startsWith("pdf")){
            throw new HttpBadRequestException("暂不支持pdf的标题获取！");
        }
//        List<SuperTitle> allHeads = wordParser.getAllHeads().stream().map(para -> StyleWrapper.wrapperTitle(para)).collect(Collectors.toList());
        List<SuperParagraph> allHeads = wordParser.getAllHeads();
        return allHeads;
    }

    @Override
    public List<SuperParagraph> getAllParaByTitleId(String token, Long paraId){
        WordParser wordParser = getResultBean(token);

//        // 检验段落ID是否在文档段落的所属范围内
//        checkParaIdInAllParas(wordParser, paraId);

        List<SuperParagraph> allHeads = wordParser.getAllHeads();
        // 检验标题ID是否在文档标题的所属范围内
        checkParaIdInAllTitles(allHeads, paraId);
        int curTitleID = allHeads.get((int) (paraId - 1)).getParagraphID();
        // 判断是否存在后一个标题
        int nextTitleID = getNextTitleId(wordParser, allHeads, paraId);
        System.out.println("标题所在段落ID区间：" + curTitleID + "  " + nextTitleID);

        // 判断是否为最后一个标题
        if(nextTitleID == wordParser.getAllParagraphs().size() - 1){
            return wordParser.getAllParagraphs().stream().filter(paragraph -> paragraph.getParagraphID() > curTitleID && paragraph.getParagraphID() <= nextTitleID).collect(Collectors.toList());

        }
        return wordParser.getAllParagraphs().stream().filter(paragraph -> paragraph.getParagraphID() > curTitleID && paragraph.getParagraphID() < nextTitleID).collect(Collectors.toList());
    }

    @Override
    public List<SuperPicture> getAllPictureByTitleId(String token, Long paraId){
        WordParser wordParser = getResultBean(token);
        List<SuperParagraph> allHeads = wordParser.getAllHeads();
        // 检验标题ID是否在文档标题的所属范围内
        checkParaIdInAllTitles(allHeads, paraId);
        int curTitleID = allHeads.get((int) (paraId - 1)).getParagraphID();
        // 判断是否存在后一个标题
        int nextTitleID = getNextTitleId(wordParser, allHeads, paraId);
        System.out.println("标题所在段落ID区间：" + curTitleID + "  " + nextTitleID);

        // 判断是否为最后一个标题
        if(nextTitleID == wordParser.getAllParagraphs().size() - 1){
            return wordParser.getAllPictures().stream().filter(paragraph -> paragraph.getParagraphID() > curTitleID && paragraph.getParagraphID() <= nextTitleID).collect(Collectors.toList());

        }
        return wordParser.getAllPictures().stream().filter(picture -> picture.getParagraphID() > curTitleID && picture.getParagraphID() < nextTitleID).collect(Collectors.toList());
    }

    @Override
    public List<SuperTable> getAllTableByTitleId(String token, Long paraId){
        WordParser wordParser = getResultBean(token);
        List<SuperParagraph> allHeads = wordParser.getAllHeads();

        // 检验标题ID是否在文档标题的所属范围内
        checkParaIdInAllTitles(allHeads, paraId);
        int curTitleID = allHeads.get((int) (paraId - 1)).getParagraphID();
        // 判断是否存在后一个标题
        int nextTitleID = getNextTitleId(wordParser, allHeads, paraId);
        System.out.println("标题所在段落ID区间：" + curTitleID + "  " + nextTitleID);

        List<SuperTable> tables = wordParser.getAllTables();
        if(tables.size() > 0){
            // 判断是否为最后一个标题
            if(nextTitleID == wordParser.getAllParagraphs().size() - 1){
                return tables.stream().filter(table -> {
                    if (table.getParagraphBefore() != null && table.getParagraphAfter() != null){
                        if(table.getParagraphBefore().getParagraphID() > curTitleID && table.getParagraphAfter().getParagraphID() <= nextTitleID){
                            return true;
                        }
                    }else if(table.getParagraphBefore() != null && table.getParagraphBefore().getParagraphID() > curTitleID && table.getParagraphBefore().getParagraphID() <= nextTitleID){
                        return true;
                    }else if(table.getParagraphAfter() != null && table.getParagraphAfter().getParagraphID() > curTitleID && table.getParagraphAfter().getParagraphID() <= nextTitleID){
                        return true;
                    }
                    return false;
                }).collect(Collectors.toList());
            }else{
                return tables.stream().filter(table -> {
                    if (table.getParagraphBefore() != null && table.getParagraphAfter() != null){
                        if(table.getParagraphBefore().getParagraphID() > curTitleID && table.getParagraphAfter().getParagraphID() < nextTitleID){
                            return true;
                        }
                    }else if(table.getParagraphBefore() != null && table.getParagraphBefore().getParagraphID() > curTitleID && table.getParagraphBefore().getParagraphID() < nextTitleID){
                        return true;
                    }else if(table.getParagraphAfter() != null && table.getParagraphAfter().getParagraphID() > curTitleID && table.getParagraphAfter().getParagraphID() < nextTitleID){
                        return true;
                    }
                    return false;
                }).collect(Collectors.toList());
            }

        }
        return null;
    }

    private void checkParaIdInAllTitles(List<SuperParagraph> allHeads, Long paraId) {
        if(paraId > allHeads.size() || paraId < 1){
            throw new HttpBadRequestException("标题的ID"+ paraId +"不在文档标题的所属范围内，应保证在1~" + allHeads.size() + "之间");
        }
    }

    private void checkParaIdInAllParas(WordParser wordParser, Long paraId) {
        List<SuperParagraph> allParagraphs = wordParser.getAllParagraphs();
        if(paraId > allParagraphs.size() || paraId < 1){
            throw new HttpBadRequestException("标题(段落)的ID"+ paraId +"不在文档标题的所属范围内，应保证在1~" + allParagraphs.size() + "之间");
        }
    }


    private int getNextTitleId(WordParser wordParser, List<SuperParagraph> allHeads, Long paraId){
        // 判断是否存在后一个标题
        if (paraId >= allHeads.size()){
            // 已经是最后一个标题，则返回最后一个段落的id
            return wordParser.getAllParagraphs().size() - 1;
        }else{
            return allHeads.get(Math.toIntExact(paraId)).getParagraphID();
        }
    }

    @Override
    public SuperParagraph getParaInfoByParaId(String token, Long paraId){
        WordParser wordParser = getResultBean(token);
        List<SuperParagraph> paragraphs = wordParser.getAllParagraphs();
        checkParaIdInAllParas(paragraphs, paraId);
        return paragraphs.get((int) (paraId - 1));
    }

    private void checkParaIdInAllParas(List<SuperParagraph> paragraphs, Long paraId) {
        if(paraId > paragraphs.size() || paraId < 1){
            throw new HttpBadRequestException("段落ID"+ paraId +"不在文档段落的所属范围内，应保证在1~" + paragraphs.size() + "之间");
        }
    }

    @Override
    public SuperFontStyle getFontStyleByParaId(String token, Long paraId){
        WordParser wordParser = getResultBean(token);
        List<SuperParagraph> paragraphs = wordParser.getAllParagraphs();
        checkParaIdInAllParas(paragraphs, paraId);
        SuperParagraph superParagraph = paragraphs.get((int) (paraId - 1));
        return StyleWrapper.wrapperFontStyle(superParagraph);
    }

    @Override
    public SuperParagraphStyle getParaStyleByParaId(String token, Long paraId){
        WordParser wordParser = getResultBean(token);
        List<SuperParagraph> paragraphs = wordParser.getAllParagraphs();
        checkParaIdInAllParas(paragraphs, paraId);
        SuperParagraph superParagraph = paragraphs.get((int) (paraId - 1));
        return StyleWrapper.wrapperParaStyle(superParagraph);
    }

    @Override
    public String deleteParserTaskByToken(String token) {
        ValueOperations valueOperations = redisTemplate.opsForValue();
        Object redisContent = valueOperations.get(token);
        Object redisContentTemplate = valueOperations.get(token + "-" + EXP_TIMES);
        if(redisContent == null && redisContentTemplate == null){
            return "token已失效！无需重复释放资源！";
        }

        if(redisContent != null){
            redisTemplate.delete(token);
        }
        if(redisContentTemplate != null){
            redisTemplate.delete(token + "-" + EXP_TIMES);
        }
        return "资源释放成功";
    }

    /**
     * 获取javabean
     * @param token
     * @return
     */
    private WordParser getResultBean(String token) {
        ValueOperations valueOperations = redisTemplate.opsForValue();
        Object redisContent = valueOperations.get(token);
        Object redisContentTemplate = valueOperations.get(token + "-" + EXP_TIMES);
        if(redisContent == null && redisContentTemplate == null){
            throw new HttpBadRequestException("token已失效！");
        }
//        else if(redisContent == null && redisContentTemplate != null){
//            throw new HttpBadRequestException("正在解析中，请耐心等待！");
//        }
        return getResultFromRedis((String) redisContent);
    }

    /**
     * 将文件解析结果存入redis中
     * @param uploadFile
     * @return
     * @throws IOException
     */
    private String parseFileContent(MultipartFile uploadFile) throws IOException {
        String fileName = uploadFile.getOriginalFilename();
        String uuid = UUID.randomUUID().toString();

        // 生成token
        String fileLowerName = fileName.toLowerCase();
        String fileType = fileLowerName.substring(fileLowerName.lastIndexOf(".") + 1, fileLowerName.length());
        String token = fileType + "-" + System.currentTimeMillis() + "-" + uuid;
        parserAsync.asyncParserFile(uploadFile, fileName, token);

        // 设置临时的redis缓存，用于判断正在解析中
        ValueOperations valueOperations = redisTemplate.opsForValue();
        valueOperations.set(token + "-" + EXP_TIMES, true, EXP_TIMES, TimeUnit.SECONDS);
        return token;
    }

    // 重redis中获取javabean
    private WordParser getResultFromRedis(String redisContent) {
        Map<String, Object> map = new HashMap<>();

        map.put("docParser", DocParser.class);
        map.put("docxParser", DocxParser.class);
        map.put("pdfParser", PdfParser.class);

        map.put("docxParagraphs", DocxParagraph.class);
        map.put("docxTables", DocxTable.class);
        map.put("docxTableContent", DocxParagraph.class);
        map.put("docxPictures", DocxPicture.class);

        map.put("docParagraphs", DocParagraph.class);
        map.put("docTables", DocTable.class);
        map.put("docPictures", DocPicture.class);
        map.put("docTableContent", DocParagraph.class);

        map.put("pdfParagraphs", PdfParagraph.class);
        map.put("pdfTables", PdfTable.class);
        map.put("pdfPictures", PdfPicture.class);
        map.put("pdfTableContent", PdfParagraph.class);

        WordParser wordParser = (WordParser) JSONObject.toBean(JSONObject.fromObject(redisContent), WordParser.class, map);
        return wordParser;
    }

    /**
     * 将JSON数据格式化并保存到文件中
     * @param jsonData 需要输出的json数
     * @param filePath 输出的文件地址
     * @return
     */
    private static boolean createJsonFile(Object jsonData, String filePath) {
        Gson gson = new Gson();
        String content = gson.toJson(jsonData);
        // 标记文件生成是否成功
        boolean flag = true;
        // 生成json格式文件
        try {
            // 保证创建一个新文件
            File file = new File(filePath);
            if (!file.getParentFile().exists()) { // 如果父目录不存在，创建父目录
                file.getParentFile().mkdirs();
            }
            if (file.exists()) { // 如果已存在,删除旧文件
                file.delete();
            }
            file.createNewFile();
            // 将格式化后的字符串写入文件
            Writer write = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
            write.write(content);
            write.flush();
            write.close();
        } catch (Exception e) {
            flag = false;
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 从json文件中获取JavaBean
     * @param token
     * @return
     */
    private WordParser getObjectFromJsonFile(String token) {
        String absolutePath = System.getProperty("user.dir");
        String path = absolutePath + "/fileTemp/";
        String filePath = path + token + ".json";
        String jsonData = readJsonData(filePath);
        Map<String, Object> map = new HashMap<>();

        map.put("docParser", DocParser.class);
        map.put("docxParser", DocxParser.class);
        map.put("pdfParser", PdfParser.class);

        map.put("docxParagraphs", DocxParagraph.class);
        map.put("docxTables", DocxTable.class);
        map.put("docxTableContent", DocxParagraph.class);
        map.put("docxPictures", DocxPicture.class);

        map.put("docParagraphs", DocParagraph.class);
        map.put("docTables", DocTable.class);
        map.put("docPictures", DocPicture.class);
        map.put("docTableContent", DocParagraph.class);

        map.put("pdfParagraphs", PdfParagraph.class);
        map.put("pdfTables", PdfTable.class);
        map.put("pdfPictures", PdfPicture.class);
        map.put("pdfTableContent", PdfParagraph.class);
        WordParser wordParser = (WordParser) JSONObject.toBean(JSONObject.fromObject(jsonData), WordParser.class, map);
        return wordParser;
    }

    private static String readJsonData(String pactFile){
        // 读取文件数据
        StringBuffer strbuffer = new StringBuffer();
        File myFile = new File(pactFile);
        if (!myFile.exists()) {
            System.err.println("Can't Find " + pactFile);
        }
        try {
            FileInputStream fis = new FileInputStream(pactFile);
            InputStreamReader inputStreamReader = new InputStreamReader(fis, "UTF-8");
            BufferedReader in  = new BufferedReader(inputStreamReader);

            String str;
            while ((str = in.readLine()) != null) {
                strbuffer.append(str);
            }
            in.close();
        } catch (IOException e) {
            e.getStackTrace();
        }
        return strbuffer.toString();
    }
}

