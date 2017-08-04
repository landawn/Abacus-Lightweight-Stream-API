package com.annimon.stream.intstreamtests;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.annimon.stream.IntStream;
import com.annimon.stream.function.IntSupplier;

public final class LimitTest {

    @Test
    public void testLimit() {
        assertTrue(IntStream.of(1, 2, 3, 4, 5, 6).limit(3).count() == 3);
        assertTrue(IntStream.generate(new IntSupplier() {

            int current = 42;

            @Override
            public int getAsInt() {
                current = current + current << 1;
                return current;
            }
        }).limit(6).count() == 6);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testLimitNegative() {
        IntStream.of(42).limit(-1).count();
    }

    @Test
    public void testLimitZero() {
        assertTrue(IntStream.of(1, 2).limit(0).count() == 0);
    }

}
