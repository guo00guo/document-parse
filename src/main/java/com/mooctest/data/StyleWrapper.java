package main.java.com.mooctest.data;

import main.java.com.mooctest.domainObject.SuperFontStyle;
import main.java.com.mooctest.domainObject.SuperParagraph;
import main.java.com.mooctest.domainObject.SuperParagraphStyle;
import org.springframework.beans.BeanUtils;

/**
 * @author guochao
 * @date 2020-05-11 18:17
 */
public class StyleWrapper {

    public static SuperFontStyle wrapperFontStyle(SuperParagraph superParagraph){
        SuperFontStyle superFontStyle = new SuperFontStyle();
        BeanUtils.copyProperties(superParagraph, superFontStyle);
        return superFontStyle;
    }

    public static SuperParagraphStyle wrapperParaStyle(SuperParagraph superParagraph){
        SuperParagraphStyle superParagraphStyle = new SuperParagraphStyle();
        BeanUtils.copyProperties(superParagraph, superParagraphStyle);
        return superParagraphStyle;
    }
}
