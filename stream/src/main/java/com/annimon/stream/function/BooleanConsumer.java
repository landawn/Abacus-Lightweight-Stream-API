package com.annimon.stream.function;

/**
 * Represents an operation on a {@code boolean}-valued input argument.
 *
 * @since 1.1.8
 * @see Consumer
 */
@FunctionalInterface
public interface BooleanConsumer {

    /**
     * Performs operation on the given argument.
     *
     * @param value  the input argument
     */
    void accept(boolean value);
}
