package com.annimon.stream.streamtests;

import static com.annimon.stream.test.hamcrest.StreamMatcher.assertElements;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.annimon.stream.IntStream;
import com.annimon.stream.LongStream;

public final class RangeTest {

    @Test
    public void testStreamRange() {
        IntStream.range(0, 5).boxed().chain(assertElements(contains(0, 1, 2, 3, 4)));
    }

    @Test
    public void testStreamRangeOnMaxValues() {
        long count = IntStream.range(Integer.MAX_VALUE - 10, Integer.MAX_VALUE).boxed().count();
        assertEquals(10L, count);
    }

    @Test
    public void testStreamRangeOnMaxLongValues() {
        long count = LongStream.range(Long.MAX_VALUE - 10, Long.MAX_VALUE).boxed().count();
        assertEquals(10L, count);
    }

    @Test
    public void testStreamRangeClosed() {
        IntStream.rangeClosed(0, 5).boxed().chain(assertElements(contains(0, 1, 2, 3, 4, 5)));
    }

    @Test
    public void testStreamRangeClosedOnMaxValues() {
        long count = IntStream.rangeClosed(Integer.MAX_VALUE - 10, Integer.MAX_VALUE).boxed().count();
        assertEquals(11L, count);
    }

    @Test
    public void testStreamRangeClosedOnMaxLongValues() {
        long count = LongStream.rangeClosed(Long.MAX_VALUE - 10, Long.MAX_VALUE).boxed().count();
        assertEquals(11L, count);
    }
}
