package com.annimon.stream.function;

/**
 * Represents an operation on a single int-valued operand that produces an int-valued result.
 * This is the primitive type specialization of UnaryOperator for int.
 */
@FunctionalInterface
public interface IntUnaryOperator {

    /**
     * Applies this operator to the given operand.
     *
     * @param operand the operand
     * @return the operator result
     */
    int applyAsInt(int operand);
}
