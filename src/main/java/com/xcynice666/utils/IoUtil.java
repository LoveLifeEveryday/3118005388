package com.xcynice666.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;


/**
 * @author xucanyou666
 * @ClassName: IoUtil
 * @Date: 2020/9/24 11:32
 * @Description: io 流操作的工具类
 */
public class IoUtil {
    
    /**
     * 将文件路径对应的 txt文本转化为内存中的字符串
     *
     * @param filepath 文件路径
     * @return 把txt文件转化成字符串
     */
    public static String convertTxt2String(String filepath) {
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
    public static void convertString2File(String outputFilePath, String result) {
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
}
