package main.java.com.mooctest.service;

import main.java.com.mooctest.domainObject.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * @author guochao
 * @date 2020-05-08 17:30
 */

public interface ParserService {
    String parserFile(MultipartFile uploadFile) throws IOException;

    List<SuperParagraph> getAllPara(String token);

    List<SuperPicture> getAllPicture(String token);

    List<SuperTable> getAllTable(String token);

    List<SuperParagraph> getAllTitle(String token);

    List<SuperParagraph> getAllParaByTitleId(String token, Long paraId);

    List<SuperPicture> getAllPictureByTitleId(String token, Long paraId);

    List<SuperTable> getAllTableByTitleId(String token, Long paraId);

    SuperParagraph getParaInfoByParaId(String token, Long paraId);

    SuperFontStyle getFontStyleByParaId(String token, Long paraId);

    SuperParagraphStyle getParaStyleByParaId(String token, Long paraId);

    String deleteParserTaskByToken(String token);
}
