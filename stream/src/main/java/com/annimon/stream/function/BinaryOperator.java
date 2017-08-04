package com.annimon.stream.function;

/**
 * Represents an operation on two operands that produces a result of the
 * same type as its operand.
 *
 * @param <T> the type of the operands and result of the operator
 */
@FunctionalInterface
public interface BinaryOperator<T> extends BiFunction<T, T, T> {

}
