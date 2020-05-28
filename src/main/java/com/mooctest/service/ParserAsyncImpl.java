package com.mooctest.service;

import com.google.gson.Gson;
import com.mooctest.domainObject.WordParser;
import com.mooctest.factory.WordParserFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @author guochao
 * @date 2020-05-15 18:30
 */
@Component
public class ParserAsyncImpl {

    @Autowired
    private RedisTemplate<Object,Object> redisTemplate;
    @Value("${redis.timeout}")
    private int EXP_TIMES;

//    @Async
    public void asyncParserFile(MultipartFile uploadFile, String fileName, String token) throws IOException {
        WordParser wordParser = WordParserFactory.createWordParser();
        wordParser.parser(uploadFile, fileName);
        // 存入redis中
        Gson gson = new Gson();
        String content = gson.toJson(wordParser);
        ValueOperations valueOperations = redisTemplate.opsForValue();
        valueOperations.set(token, content, EXP_TIMES, TimeUnit.SECONDS);
    }
}
