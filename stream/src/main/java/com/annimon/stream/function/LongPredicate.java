package com.annimon.stream.function;

/**
 * Represents a {@code long}-valued predicate (function with boolean type result).
 *
 * @since 1.1.4
 * @see Predicate
 */
@FunctionalInterface
public interface LongPredicate {

    /**
     * Tests the value for satisfying predicate.
     *
     * @param value  the value to be tests
     * @return {@code true} if the value matches the predicate, otherwise {@code false}
     */
    boolean test(long value);
}
