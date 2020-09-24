package com.xcynice666.main;


import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.common.Term;
import com.xcynice666.bean.AtomicNum;
import com.xcynice666.bean.WordGroup;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @Author xcynice666
 * @Description 主程序入口
 * @Date 2020/9/24 11:14
 **/

public class MainEntrance {

    /**
     * 主程序入口
     *
     * @param args 传入的文件的路径数组
     */
    public static void main(String[] args) {
        checkSimilarity(args[0], args[1], args[2]);
        //正常退出
        System.exit(0);
    }

    /**
     * 主方法，暴露给外层，检查相似度
     *
     * @param orgTextPath 原文文件路径
     * @param newTextPath 抄袭文件路径
     * @param ansFilePath 答案文件路径
     */
    public static void checkSimilarity(String orgTextPath, String newTextPath, String ansFilePath) {
        File originFile = new File(orgTextPath);
        File newFile = new File(newTextPath);
        if (!originFile.exists() || !newFile.exists()) {
            System.out.println("文件路径无效，请检查文件路径参数!\n");
            return;
        }
        // 执行计算，得到答案
        double answer = getSimilarity(convertTxt2String(orgTextPath),
                convertTxt2String(newTextPath));
        // 把结果字符串输出到指定的文件中
        String ansString = formatPrint(answer);
        convertString2File(ansFilePath, orgTextPath + "\n" + newTextPath + "\n"
                + "相似度 ：" + ansString);
        //在控制台中输出相似度
        System.out.println("相似度 : " + ansString + "\n");
    }

    /**
     * 使用 HanLp 框架进行分词处理
     *
     * @param sentence 文章的 string
     * @return 分词的集合 List<WordGroup>
     */
    public static List<WordGroup> string2WordList(String sentence) {

        // 1、 采用HanLP标准分词进行分词
        List<Term> termList = HanLP.segment(sentence);
        // 2、重新封装到Word对象中
        return termList.stream().map(term ->
                new WordGroup(term.word, term.nature.toString())).collect(Collectors.toList());

    }


    /**
     * 格式化打印,将相似度转为百分数
     *
     * @param similarity 相似度
     * @return 百分数的相似度
     */
    public static String formatPrint(double similarity) {
        BigDecimal decimal = new BigDecimal(String.valueOf(similarity * 100.0));
        return decimal.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString();
    }

    /**
     * 将文件路径对应的 txt文本转化为内存中的字符串
     *
     * @param filepath 文件路径
     * @return 把txt文件转化成字符串
     */
    private static String convertTxt2String(String filepath) {
        String str = "";
        File file = new File(filepath);
        if (!file.exists()) {
            System.out.println(filepath + "此文件不存在! ");
            return "";
        }
        try {
            FileInputStream inputStream = new FileInputStream(file);
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            int bufferCount = inputStream.read(buffer);
            if (bufferCount == 0) {
                System.out.println(filepath + " 是空文本");
            }
            //关闭输入流，否则可能会内存泄漏
            inputStream.close();
            //设置输出为 UTF-8 的格式，防止乱码
            str = new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return str;
    }


    /**
     * 输出文件
     *
     * @param outputFilePath 输出文件的路径
     * @param result         字符串形式传入结果
     */
    private static void convertString2File(String outputFilePath, String result) {
        // 创建输出路径的文件
        File outputFile = new File(outputFilePath);
        // 写入
        try (FileWriter fr = new FileWriter(outputFile)) {
            char[] c = result.toCharArray();
            fr.write(c);
            //记得关闭
            fr.close();
            System.out.println("结果已被写入：" + outputFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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
        List<WordGroup> words1 = string2WordList(string1);
        List<WordGroup> words2 = string2WordList(string2);

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
        AtomicNum ab = new AtomicNum();
        // a的平方
        AtomicNum aa = new AtomicNum();
        // b的平方
        AtomicNum bb = new AtomicNum();

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
    private static void getWordFrequencyVector(Set<WordGroup> wordGroups, Map<String, Float> weightMap1, Map<String, Float> weightMap2, AtomicNum ab, AtomicNum aa, AtomicNum bb) {
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
