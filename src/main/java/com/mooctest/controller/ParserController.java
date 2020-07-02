package com.mooctest.controller;

import com.mooctest.data.response.ResponseVO;
import com.mooctest.data.response.ServerCode;
import com.mooctest.domainObject.*;
import com.mooctest.exception.HttpBadRequestException;
import com.mooctest.service.ParserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author guochao
 * @date 2020-05-08 15:46
 */
@RestController
@RequestMapping("/word_parser")
@Slf4j
@Api(value="parser",tags="文档解析的相关接口")
public class ParserController {

    @Autowired
    private ParserService parserService;

    @RequestMapping(value = "/load_file", method = RequestMethod.POST, headers = "content-type=multipart/form-data")
    @ApiOperation(value = "上传并解析文档", notes = "上传并解析文档，需参数")
    @ResponseBody
    public ResponseVO<Map<String, List<String>>> loadFile(@ApiParam(value = "上传的文件", required = true)
                                               @RequestParam(value = "uploadFileList", required = true) MultipartFile uploadFileList) throws IOException {
        Map<String, List<String>> map = new HashMap<>();
        List<String> tokenList = new ArrayList<>();
//        if(uploadFileList != null && uploadFileList.length > 0){
//            for(MultipartFile uploadFile : uploadFileList){
//                String token = parserService.parserFile(uploadFile);
//                tokenList.add(token);
//            }
//        }else{
//            System.out.println("No file is uploaded!");
//            throw new HttpBadRequestException("No file is uploaded!");
//        }
        if(uploadFileList != null){
            String token = parserService.parserFile(uploadFileList);
            tokenList.add(token);
        }else{
            System.out.println("No file is uploaded!");
            throw new HttpBadRequestException("No file is uploaded!");
        }

        map.put("token", tokenList);
//        log.info("上传并解析文档,token：{}",map.toString());
        return new ResponseVO<>(ServerCode.SUCCESS,map);
    }

    @RequestMapping(value = "/{token}/all_paragraphs", method = RequestMethod.GET)
    @ApiOperation(value="获取文档的所有段落信息",notes="获取文档的所有段落信息，需将获得的token作为参数", httpMethod = "GET")
    @ResponseBody
    public ResponseVO<List<SuperParagraph>> getAllPara(@PathVariable(name = "token") String token){
//        log.info("获取文档的所有段落信息,token：{}",token);
        return new ResponseVO<>(ServerCode.SUCCESS,parserService.getAllPara(token));
    }

    @RequestMapping(value = "/{token}/all_pics", method = RequestMethod.GET)
    @ApiOperation(value="获取文档的所有图片信息",notes="获取文档的所有图片信息，需将获得的token作为参数", httpMethod = "GET")
    @ResponseBody
    public ResponseVO<List<SuperPicture>> getAllPic(@PathVariable(name = "token") String token) {
//        log.info("获取文档的所有图片信息,token：{}",token);
        return new ResponseVO<>(ServerCode.SUCCESS,parserService.getAllPicture(token));
    }

    @RequestMapping(value = "/{token}/all_tables", method = RequestMethod.GET)
    @ApiOperation(value="获取文档的所有表格信息",notes="获取文档的所有表格信息，需将获得的token作为参数", httpMethod = "GET")
    @ResponseBody
    public ResponseVO<List<SuperTable>> getAllTable(@PathVariable(name = "token") String token) {
//        log.info("获取文档的所有表格信息,token：{}",token);
        return new ResponseVO<>(ServerCode.SUCCESS,parserService.getAllTable(token));
    }

    @RequestMapping(value = "/{token}/all_titles", method = RequestMethod.GET)
    @ApiOperation(value="获取文档的所有标题信息",notes="获取文档的所有标题信息，需将获得的token作为参数", httpMethod = "GET")
    @ResponseBody
    public ResponseVO<List<SuperParagraph>> getAllTitle(@PathVariable(name = "token") String token) {
//        log.info("获取文档的所有标题信息,token：{}",token);
        return new ResponseVO<>(ServerCode.SUCCESS,parserService.getAllTitle(token));
    }

    /**
     * 获取指定段落下的段落信息
     * @param token
     * @param paraId
     * @return
     */
    @RequestMapping(value = "/{token}/paragraph/{paragraph_id}", method = RequestMethod.GET)
    @ApiOperation(value="获取文档指定段落的段落信息",notes="获取文档指定标题下的所有段落信息，需将获得的token作为参数", httpMethod = "GET")
    @ResponseBody
    public ResponseVO<SuperParagraph> getParaInfoByParaId(@PathVariable(name = "token") String token, @PathVariable("paragraph_id") Long paraId) {
//        log.info("文档指定段落的段落信息,token：{}, paragraphId:{}",token, paraId);
        return new ResponseVO<>(ServerCode.SUCCESS,parserService.getParaInfoByParaId(token, paraId));
    }

    @RequestMapping(value = "/{token}/paragraph/{paragraph_id}/paragraph_style", method = RequestMethod.GET)
    @ApiOperation(value="获取文档指定段落的段落格式信息",notes="获取文档指定段落的段落格式信息，需将获得的token作为参数", httpMethod = "GET")
    @ResponseBody
    public ResponseVO<SuperParagraphStyle> getParaStyleByParaId(@PathVariable(name = "token") String token, @PathVariable("paragraph_id") Long paraId) {
//        log.info("文档指定段落的段落格式信息,token：{}, paragraphId:{}",token, paraId);
        return new ResponseVO<>(ServerCode.SUCCESS,parserService.getParaStyleByParaId(token, paraId));
    }

    @RequestMapping(value = "/{token}/paragraph/{paragraph_id}/font_style", method = RequestMethod.GET)
    @ApiOperation(value="获取文档指定段落的字体格式信息",notes="获取文档指定段落的字体格式信息，需将获得的token作为参数", httpMethod = "GET")
    @ResponseBody
    public ResponseVO<SuperFontStyle> getFontStyleByParaId(@PathVariable(name = "token") String token, @PathVariable("paragraph_id") Long paraId) {
//        log.info("文档指定段落的字体格式信息,token：{}, paragraphId:{}",token, paraId);
        return new ResponseVO<>(ServerCode.SUCCESS,parserService.getFontStyleByParaId(token, paraId));
    }

    /**
     * 获取指定标题下的所有段落信息
     * @param token
     * @param paraId
     * @return
     */
    @RequestMapping(value = "/{token}/title/{paragraph_id}/all_paragraphs", method = RequestMethod.GET)
    @ApiOperation(value="获取文档指定标题下的所有段落信息",notes="获取文档指定标题下的所有段落信息，需将获得的token作为参数", httpMethod = "GET")
    @ResponseBody
    public ResponseVO<List<SuperParagraph>> getAllParaByTitleId(@PathVariable(name = "token") String token, @PathVariable("paragraph_id") Long paraId) {
//        log.info("获取文档指定标题下的所有段落信息,token：{}, titleId:{}",token, paraId);
        return new ResponseVO<>(ServerCode.SUCCESS,parserService.getAllParaByTitleId(token, paraId));
    }

    @RequestMapping(value = "/{token}/title/{paragraph_id}/all_pics", method = RequestMethod.GET)
    @ApiOperation(value="获取文档指定标题下的所有图片信息",notes="获取文档指定标题下的所有图片信息，需将获得的token作为参数", httpMethod = "GET")
    @ResponseBody
    public ResponseVO<List<SuperPicture>> getAllPictureByTitleId(@PathVariable(name = "token") String token, @PathVariable("paragraph_id") Long paraId) {
//        log.info("获取文档指定标题下的所有图片信息,token：{}, titleId:{}",token, paraId);
        return new ResponseVO<>(ServerCode.SUCCESS,parserService.getAllPictureByTitleId(token, paraId));
    }

    @RequestMapping(value = "/{token}/title/{paragraph_id}/all_table", method = RequestMethod.GET)
    @ApiOperation(value="获取文档指定标题下的所有表格信息",notes="获取文档指定标题下的所有表格信息，需将获得的token作为参数", httpMethod = "GET")
    @ResponseBody
    public ResponseVO<List<SuperTable>> getAllTableByTitleId(@PathVariable(name = "token") String token, @PathVariable("paragraph_id") Long paraId) {
//        log.info("获取文档指定标题下的所有表格信息,token：{}, titleId:{}",token, paraId);
        return new ResponseVO<>(ServerCode.SUCCESS,parserService.getAllTableByTitleId(token, paraId));
    }

    @RequestMapping(value = "/{token}", method = RequestMethod.DELETE)
    @ApiOperation(value="释放服务端资源",notes="释放服务端资源，需将获得的token作为参数", httpMethod = "DELETE")
    @ResponseBody
    public ResponseVO<String> deleteParserTaskByToken(@PathVariable(name = "token") String token) {
//        log.info("释放服务端资源,token：{}",token);
        return new ResponseVO<>(ServerCode.SUCCESS,parserService.deleteParserTaskByToken(token));
    }
}
