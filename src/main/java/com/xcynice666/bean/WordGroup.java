package com.xcynice666.bean;

import lombok.Data;

import java.util.Objects;

/**
 * @author xucanyou666
 * @ClassName: WordGroup
 * @Date: 2020/9/24 11:12
 * @Description: 词组的实体类
 */

@Data
public class WordGroup implements Comparable<WordGroup> {


    /**
     * 词语名
     */
    private final String name;


    /**
     * 词性
     */
    private final String posClass;


    /**
     * 权重 -- 用于词向量分析
     */
    private Float weight;

    public WordGroup(String name, String posClass) {
        this.name = name;
        this.posClass = posClass;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.name);
    }

    /**
     * 重写 equals 方法，当词语名相同则相同
     *
     * @param obj 对象
     * @return 是否相同
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final WordGroup other = (WordGroup) obj;
        return Objects.equals(this.name, other.name);
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        if (name != null) {
            str.append(name);
        }
        if (posClass != null) {
            str.append("/").append(posClass);
        }
        return str.toString();
    }


    @Override
    public int compareTo(WordGroup o) {
        if (this == o) {
            return 0;
        }
        if (this.name == null) {
            return -1;
        }
        if (o == null) {
            return 1;
        }
        String t = o.getName();
        if (t == null) {
            return 1;
        }
        return this.name.compareTo(t);
    }

    public String getName() {
        return name;
    }

    public Float getWeight() {
        return weight;
    }

    public void setWeight(Float weight) {
        this.weight = weight;
    }
}
