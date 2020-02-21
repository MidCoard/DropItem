package com.focess.dropitem.util;

public class Pair<E, T> {

    public static <E, T> Pair<E, T> of(final E a, final T b) {
        return new Pair<>(a, b);
    }

    private final E first;

    private final T second;

    private Pair(final E first, final T second) {
        this.first = first;
        this.second = second;
    }

    public E getKey() {
        return this.first;
    }

    public T getValue() {
        return this.second;
    }

}
