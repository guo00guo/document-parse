package com.mooctest.factory;

import com.mooctest.domainObject.WordParser;

/**
 * @author guochao
 * @date 2020-05-08 18:55
 */
public class WordParserFactory {
    public static WordParser createWordParser() {
        return new WordParser();
    }
}
