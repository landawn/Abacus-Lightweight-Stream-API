package com.annimon.stream.function;

/**
 * Represents an operation on two input arguments.
 *
 * @param <T> the type of the first argument
 * @param <U> the type of the second argument
 * @see Consumer
 */
@FunctionalInterface
public interface BiConsumer<T, U> {

    /**
     * Performs operation on two arguments.
     *
     * @param value1  the first argument
     * @param value2  the second argument
     */
    void accept(T value1, U value2);
}
