package com.annimon.stream.function;

/**
 * Represents an operation on a single {@code long}-valued operand
 * that produces a {@code long}-valued result.
 *
 * @since 1.1.4
 * @see UnaryOperator
 */
@FunctionalInterface
public interface LongUnaryOperator {

    /**
     * Applies this operator to the given operand.
     *
     * @param operand the operand
     * @return the operator result
     */
    long applyAsLong(long operand);
}
