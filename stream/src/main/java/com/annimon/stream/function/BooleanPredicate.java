package com.annimon.stream.function;

/**
 * Represents a {@code boolean}-valued predicate (function with boolean type result).
 *
 * @since 1.1.8
 * @see Predicate
 * @see UnaryOperator
 */
@FunctionalInterface
public interface BooleanPredicate {

    /**
     * Tests the value for satisfying predicate.
     *
     * @param value  the value to be tests
     * @return {@code true} if the value matches the predicate, otherwise {@code false}
     */
    boolean test(boolean value);
}
