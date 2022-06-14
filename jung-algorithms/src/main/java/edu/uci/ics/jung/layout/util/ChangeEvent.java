package edu.uci.ics.jung.layout.util;

/**
 * @param <T>
 * @author Tom Nelson
 */
public class ChangeEvent<T> {

    private final T source;

    public ChangeEvent(T source) {
        this.source = source;
    }

    public T getSource() {
        return source;
    }
}
