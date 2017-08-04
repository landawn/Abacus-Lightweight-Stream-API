package com.annimon.stream.function;

/**
 * Represents a predicate (function with boolean type result).
 *
 * @param <T> the type of the input to the function
 */
@FunctionalInterface
public interface BiPredicate<T, U> {

    /**
     * Tests the value for satisfying predicate.
     *
     * @param value  the value to be tests
     * @return {@code true} if the value matches the predicate, otherwise {@code false}
     */
    boolean test(T t, U u);
}
