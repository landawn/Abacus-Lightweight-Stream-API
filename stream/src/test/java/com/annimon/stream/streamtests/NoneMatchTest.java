package com.annimon.stream.streamtests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.annimon.stream.Functions;
import com.annimon.stream.IntStream;
import com.annimon.stream.Stream;

public final class NoneMatchTest {

    @Test
    public void testNoneMatchWithFalseResult() {
        boolean match = IntStream.range(0, 10).boxed().noneMatch(Functions.remainder(2));
        assertFalse(match);
    }

    @Test
    public void testNoneMatchWithTrueResult() {
        boolean match = Stream.of(2, 3, 5, 8, 13).noneMatch(Functions.remainder(10));
        assertTrue(match);
    }
}
