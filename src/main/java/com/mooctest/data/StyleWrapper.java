package com.mooctest.data;

import com.mooctest.domainObject.SuperFontStyle;
import com.mooctest.domainObject.SuperParagraph;
import com.mooctest.domainObject.SuperParagraphStyle;
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
