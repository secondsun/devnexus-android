package org.jboss.aerogear.devnexus2015.util;

/**
 * Created by summers on 1/17/16.
 */
public class Optional<T> {
    private final T item;

    private static final Optional NULL = new Optional(null);

    private Optional(T item) {
        this.item = item;
    }

    public static <T> Optional<T> nullOption() {
        return NULL;
    }

    public static <T> Optional<T> newOption(T value) {
        if (value == null) {
            return NULL;
        }
        return new Optional<>(value);
    }

    public T getItem() {
        return item;
    }

    public boolean hasValue() {
        return item != null;
    }

}
