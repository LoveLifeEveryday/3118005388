package com.xcynice666;


import com.xcynice666.utils.IoUtil;
import com.xcynice666.utils.SimilarCalculator;
import com.xcynice666.utils.TextUtil;

import java.io.File;

/**
 * @Author xcynice666
 * @Description 主程序
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
        double answer = SimilarCalculator.getSimilarity(IoUtil.convertTxt2String(orgTextPath),
                IoUtil.convertTxt2String(newTextPath));
        // 把结果字符串输出到指定的文件中
        String ansString = TextUtil.formatPrint(answer);
        IoUtil.convertString2File(ansFilePath, orgTextPath + "\n" + newTextPath + "\n"
                + "相似度 ：" + ansString);
        //在控制台中输出相似度
        System.out.println("相似度 : " + ansString + "\n");
    }


}
