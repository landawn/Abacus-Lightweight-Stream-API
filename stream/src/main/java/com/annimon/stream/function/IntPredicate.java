package com.annimon.stream.function;

/**
 * Represents a predicate (function with boolean type result).
 */
@FunctionalInterface
public interface IntPredicate {

    /**
     * Tests the value for satisfying predicate.
     *
     * @param value  the value to be tests
     * @return {@code true} if the value matches the predicate, otherwise {@code false}
     */
    boolean test(int value);
}
