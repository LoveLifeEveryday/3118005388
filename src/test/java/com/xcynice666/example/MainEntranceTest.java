package com.xcynice666.example;

import com.xcynice666.main.MainEntrance;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;


/**
 * @Author xcynice666
 * @Description 单元测试
 * @Date 2020/9/24 17:57
 **/

public class MainEntranceTest {

    @BeforeClass
    public static void beforeTest() {
        System.out.println("测试马上开始啦");
    }

    @AfterClass
    public static void afterTest() {
        System.out.println("测试已经结束啦");
    }


    /**
     * test 空文本
     */
    @Test
    public void testForEmpty() {
        test("src/test/testcase/orig.txt", "src/test/testcase/empty.txt", "src/test/result/testEmptyResult.txt");
    }

    /**
     * test 对比文本路径参数错误的情况
     */
    @Test
    public void testForWrongOriginArgument() {
        test("src/test/testcase/123.txt", "src/test/testcase/orig_0.8_add.txt", "src/test/result/testAddResult");
    }


    /**
     * 测试20%文本添加
     */
    @Test
    public void testForAdd() {
        test("src/test/testcase/orig.txt", "src/test/testcase/orig_0.8_add.txt", "src/test/result/testAddResult.txt");
    }

    /**
     * 测试20%文本删除
     */
    @Test
    public void testForDel() {
        test("src/test/testcase/orig.txt", "src/test/testcase/orig_0.8_del.txt", "src/test/result/testDelResult.txt");
    }

    /**
     * 测试20%文本乱序
     */
    @Test
    public void testForDis1() {
        test("src/test/testcase/orig.txt", "src/test/testcase/orig_0.8_dis_1.txt", "src/test/result/testDis1Result.txt");
    }

    /**
     * 20%文本乱序情况
     */
    @Test
    public void testForDis3() {
        test("src/test/testcase/orig.txt", "src/test/testcase/orig_0.8_dis_3.txt", "src/test/result/testDis3Result.txt");
    }

    /**
     * 20%文本乱序情况
     */
    @Test
    public void testForDis7() {
        test("src/test/testcase/orig.txt", "src/test/testcase/orig_0.8_dis_7.txt", "src/test/result/testDis7Result.txt");
    }



    /**
     * 测试20%文本乱序情况：orig_0.8_dis_15.txt
     */
    @Test
    public void testForDis15() {
        test("src/test/testcase/orig.txt", "src/test/testcase/orig_0.8_dis_15.txt", "src/test/result/testDis15Result.txt");
    }

    /**
     * 测试20%文本格式错乱情况：orig_0.8_mix.txt
     */
    @Test
    public void testForMix() {
        test("src/test/testcase/orig.txt", "src/test/testcase/orig_0.8_mix.txt", "src/test/result/testMixResult.txt");
    }

    /**
     * 测试20%文本错别字情况
     */
    @Test
    public void testForRep() {
        test("src/test/testcase/orig.txt", "src/test/testcase/orig_0.8_rep.txt", "src/test/result/testRepResult.txt");
    }

    /**
     * 测试相同 txt
     */
    @Test
    public void testForSame() {
        test("src/test/testcase/orig.txt", "src/test/testcase/orig.txt", "src/test/result/testSameResult.txt");
    }


    /**
     * 测试的方法
     *
     * @param orgTextPath 原先文章路径
     * @param newTextPath 新文章路径
     * @param outTextPath 输出的结果路径
     */
    private void test(String orgTextPath, String newTextPath, String outTextPath) {
        try {
            MainEntrance.checkSimilarity(orgTextPath, newTextPath, outTextPath);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

}
