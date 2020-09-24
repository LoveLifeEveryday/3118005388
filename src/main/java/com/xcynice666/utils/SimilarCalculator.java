package com.xcynice666.utils;

import com.xcynice666.bean.AtomicFloat;
import com.xcynice666.bean.WordGroup;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * @author xucanyou666
 * @ClassName: SimilarTextCalculator
 * @Date: 2020/9/24 11:34
 * @Description: 相似度计算工具类
 */
public class SimilarCalculator {


    /**
     * 计算两个字符串的相似度
     *
     * @param string1 字符串1
     * @param string2 字符串2
     * @return 相似度
     */
    public static double getSimilarity(String string1, String string2) {
        boolean isBlank1 = StringUtils.isBlank(string1);
        boolean isBlank2 = StringUtils.isBlank(string2);

        //这个代表如果两个字符串相等那当然返回1了
        if (string1.equalsIgnoreCase(string2)) {
            return 1.00;
        }
        //如果内容为空，或者字符长度为0，则代表完全相同
        if (isBlank1 && isBlank2) {
            return 1.00;
        }

        //如果一个为0或者空，一个不为，那说明完全不相似
        if (isBlank1 || isBlank2) {
            return 0.0;
        }

        //第一步：进行分词
        List<WordGroup> words1 = TextUtil.string2WordList(string1);
        List<WordGroup> words2 = TextUtil.string2WordList(string2);

        return getSimilarity(words1, words2);
    }


    /**
     * 文本相似度计算 比较了三种方式，最终决定使用余弦相似度的方式实现该需求
     *
     * @param wordGroups1 分词1
     * @param wordGroups2 分词2
     * @return 文本相似度
     */
    public static double getSimilarity(List<WordGroup> wordGroups1, List<WordGroup> wordGroups2) {

        // 1.向每一个Word对象的属性都注入weight（权重）属性值
        getWeightByFrequency(wordGroups1, wordGroups2);

        // 2.计算词频
        Map<String, Float> weightMap1 = getFastSearchMap(wordGroups1);
        Map<String, Float> weightMap2 = getFastSearchMap(wordGroups2);

        //将所有词都装入set容器中
        Set<WordGroup> wordGroups = new HashSet<>();
        wordGroups.addAll(wordGroups1);
        wordGroups.addAll(wordGroups2);
        // a.b
        AtomicFloat ab = new AtomicFloat();
        // a的平方
        AtomicFloat aa = new AtomicFloat();
        // b的平方
        AtomicFloat bb = new AtomicFloat();

        // 3.得到词频向量，后进行计算
        getWordFrequencyVector(wordGroups, weightMap1, weightMap2, ab, aa, bb);

        BigDecimal aabb = BigDecimal.valueOf(Math.sqrt(aa.doubleValue())).multiply(BigDecimal.valueOf(Math.sqrt(bb.doubleValue())));

        // 相似度=a.b/|a|*|b|
        return BigDecimal.valueOf(ab.get()).divide(aabb, 9, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     * 得到词频向量
     *
     * @param wordGroups 词组
     * @param weightMap1 词权 map1
     * @param weightMap2 词权 map2
     * @param ab         a*b
     * @param aa         a*a
     * @param bb         b*b
     */
    private static void getWordFrequencyVector(Set<WordGroup> wordGroups, Map<String, Float> weightMap1, Map<String, Float> weightMap2, AtomicFloat ab, AtomicFloat aa, AtomicFloat bb) {
        wordGroups.parallelStream().forEach(wordGroup -> {
            Float x1 = weightMap1.get(wordGroup.getName());
            Float x2 = weightMap2.get(wordGroup.getName());
            float oneDimension;
            if (x1 != null && x2 != null) {
                //x1x2
                oneDimension = x1 * x2;
                //+
                ab.addAndGet(oneDimension);
            }
            if (x1 != null) {
                //(x1)^2
                oneDimension = x1 * x1;
                //+
                aa.addAndGet(oneDimension);
            }
            if (x2 != null) {
                //(x2)^2
                oneDimension = x2 * x2;
                //+
                bb.addAndGet(oneDimension);
            }
        });
    }


    /**
     * 注入权重属性值
     *
     * @param words1 词组1
     * @param words2 词组2
     */
    protected static void getWeightByFrequency(List<WordGroup> words1, List<WordGroup> words2) {
        if (words1.get(0).getWeight() != null && words2.get(0).getWeight() != null) {
            return;
        }
        getWeightByList(words1);
        getWeightByList(words2);
    }


    /**
     * 通过 List 得到权值
     *
     * @param wordGroupList wordGroupList
     */
    private static void getWeightByList(List<WordGroup> wordGroupList) {
        Map<String, AtomicInteger> frequency = getFrequency(wordGroupList);
        wordGroupList.parallelStream().forEach(wordGroup -> wordGroup.setWeight(frequency.get(wordGroup.getName()).floatValue()));
    }


    /**
     * 统计词频
     *
     * @param wordGroups 抽离出来的分词列表
     * @return 词频 map
     */
    private static Map<String, AtomicInteger> getFrequency(List<WordGroup> wordGroups) {
        Map<String, AtomicInteger> freq = new HashMap<>();
        wordGroups.forEach(i -> freq.computeIfAbsent(i.getName(), k -> new AtomicInteger()).incrementAndGet());
        return freq;
    }


    /**
     * 构造权重快速搜索容器
     *
     * @param wordGroups 抽离出来的分词列表
     * @return 权重快速搜索容器
     */
    protected static Map<String, Float> getFastSearchMap(List<WordGroup> wordGroups) {
        //词语列表为空
        if (wordGroups == null || wordGroups.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<String, Float> weightMap = new ConcurrentHashMap<>(wordGroups.size());
        wordGroups.parallelStream().forEach(i -> {
            if (i.getWeight() != null) {
                weightMap.put(i.getName(), i.getWeight());
            } else {
                System.out.println("无词权信息:" + i.getName());
            }
        });
        return weightMap;
    }
}
