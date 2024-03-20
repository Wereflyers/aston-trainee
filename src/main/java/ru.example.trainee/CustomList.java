package ru.example.trainee;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;

public class CustomList<E> {
    private static final int DEFAULT_CAPACITY = 10;
    private static final Object[] EMPTY_ELEMENT_DATA = {};
    private Object[] data;
    private int size;

    public CustomList(int capacity) {
        size = capacity;
        data = EMPTY_ELEMENT_DATA;
    }
    public CustomList() {
        size = DEFAULT_CAPACITY;
        data = EMPTY_ELEMENT_DATA;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    public E get(int index) {
        Objects.checkIndex(index, size);
        return (E) data[index];
    }

    public boolean add(E e) {
        add(e, data, size);
        return true;
    }

    public void add(int index, E element) {
        Objects.checkIndex(index, size);
        Object[] elementData;
        if (size == (elementData = this.data).length)
            elementData = grow();
        System.arraycopy(elementData, index,
                elementData, index + 1,
                size - index);
        elementData[index] = element;
        size = size + 1;
    }

    public void clear() {
        data = EMPTY_ELEMENT_DATA;
    }

    public void remove(Object object) {
        for (int i = 0; i < size; i++) {
            if (data[i] == object) {
                remove(i);
                return;
            }
        }
    }

    public void remove(int index) {
        Objects.checkIndex(index, size);
        for (int i = index; i < size - 1; i++) {
            data[i] = data[i + 1];
        }
        data[size - 1] = null;
        size = size - 1;
    }

    private void add(E e, Object[] elementData, int s) {
        if (s == elementData.length)
            elementData = grow();
        elementData[s] = e;
        size = s + 1;
    }

    public void sort(Comparator<? super E> c) {
        Arrays.sort((E[]) data, 0, size, c);
    }

    private Object[] grow() {
        return data = Arrays.copyOf(data, size + size / 2);
    }
}