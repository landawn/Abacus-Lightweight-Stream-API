package com.annimon.stream.function;

/**
 * Represents an operation on a single {@code double}-valued operand
 * that produces a {@code double}-valued result.
 *
 * @since 1.1.4
 * @see UnaryOperator
 */
@FunctionalInterface
public interface DoubleUnaryOperator {

    /**
     * Applies this operator to the given operand.
     *
     * @param operand the operand
     * @return the operator result
     */
    double applyAsDouble(double operand);
}
