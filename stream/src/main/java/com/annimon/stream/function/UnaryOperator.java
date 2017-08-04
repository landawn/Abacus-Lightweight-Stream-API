package com.annimon.stream.function;

/**
 * Operation on a single operand that produces a result of the
 * same type as its operand.
 *
 * @param <T> the type of the operand and result of the operator
 */
@FunctionalInterface
public interface UnaryOperator<T> extends Function<T, T> {
}
