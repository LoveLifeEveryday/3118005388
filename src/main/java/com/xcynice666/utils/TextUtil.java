package com.xcynice666.utils;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.common.Term;
import com.xcynice666.bean.WordGroup;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


/**
 * @author xucanyou666
 * @ClassName: TextUtil
 * @Date: 2020/9/24 11:12
 * @Description: 文本处理工具类
 */
public class TextUtil {
    
    /**
     * 使用 HanLp 框架进行分词处理
     *
     * @param sentence 文章的 string
     * @return 分词的集合 List<WordGroup>
     */
    public static List<WordGroup> string2WordList(String sentence) {
        List<Term> termList = HanLP.segment(sentence);
        List<WordGroup> wordGroupList = new ArrayList<>();
        for (Term term : termList) {
            wordGroupList.add(new WordGroup(term.word, term.nature.toString()));
        }
        return wordGroupList;
    }


    /**
     * 格式化打印,将相似度转为百分数
     *
     * @param similarity 相似度
     * @return 百分数的相似度
     */
    public static String formatPrint(double similarity) {
        double similarityRatio = similarity * 100.0;
        BigDecimal bigDecimal = new BigDecimal(String.valueOf(similarityRatio));
        return bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString();
    }

}
