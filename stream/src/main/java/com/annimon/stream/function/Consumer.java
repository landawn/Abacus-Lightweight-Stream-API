package com.annimon.stream.function;

/**
 * Represents an operation on input argument.
 *
 * @param <T> the type of the input to the operation
 * @see BiConsumer
 */
@FunctionalInterface
public interface Consumer<T> {

    /**
     * Performs operation on argument.
     *
     * @param t  the input argument
     */
    void accept(T t);
}
