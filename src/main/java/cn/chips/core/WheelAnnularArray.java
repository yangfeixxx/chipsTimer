package cn.chips.core;

import java.util.concurrent.ConcurrentLinkedQueue;

public class WheelAnnularArray<T> {
    private int index;
    private int size;
    private transient Object[] arr;
    private static final int DEFAULT_SIZE = 16;

    public WheelAnnularArray() {
        this(DEFAULT_SIZE);
    }

    public WheelAnnularArray(int size) {
        this.arr = new Object[size];
        this.size = size;
        this.index = 0;
    }

    public void add(Object obj) {
        arr[index] = obj;
        index = index == size - 1 ? 0 : ++index;
    }

    public void add(Object obj, int index) {
        arr[index] = obj;

    }

    public T get(int index) {
        return (T) arr[index];
    }

    public int getIndex() {
        return this.index;
    }

    public int getSize() {
        return this.size;
    }

}
