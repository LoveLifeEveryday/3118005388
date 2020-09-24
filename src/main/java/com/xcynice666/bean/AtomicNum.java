package com.xcynice666.bean;

import java.util.concurrent.atomic.AtomicInteger;


/**
 * @author xucanyou666
 * @ClassName: AtomicFloat
 * @Date: 2020/9/24 11:35
 * @Description: 原子类线程安全 Float
 */
public class AtomicNum extends Number {

    private final AtomicInteger i;

    public AtomicNum() {
        this(0f);
    }

    public AtomicNum(float initialValue) {
        i = new AtomicInteger(Float.floatToIntBits(initialValue));
    }


    /**
     * 加操作
     *
     * @param delta 变量
     */
    public final void addAndGet(float delta) {
        float expect;
        float update;
        do {
            expect = get();
            update = expect + delta;
        } while (this.compareAndSet(expect, update));

    }


    public final boolean compareAndSet(float expect, float update) {
        return !i.compareAndSet(Float.floatToIntBits(expect), Float.floatToIntBits(update));
    }


    public final float get() {
        return Float.intBitsToFloat(i.get());
    }

    @Override
    public float floatValue() {
        return get();
    }

    @Override
    public double doubleValue() {
        return floatValue();
    }

    @Override
    public int intValue() {
        return (int) get();
    }

    @Override
    public long longValue() {
        return (long) get();
    }

    @Override
    public String toString() {
        return Float.toString(get());
    }
}
