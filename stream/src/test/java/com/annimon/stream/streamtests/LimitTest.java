package com.annimon.stream.streamtests;

import static com.annimon.stream.test.hamcrest.StreamMatcher.assertElements;
import static com.annimon.stream.test.hamcrest.StreamMatcher.isEmpty;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.annimon.stream.IntStream;
import com.annimon.stream.Stream;

public final class LimitTest {

    @Test
    public void testLimit() {
        IntStream.range(0, 10).boxed().limit(2).chain(assertElements(contains(0, 1)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testLimitNegative() {
        IntStream.range(0, 10).boxed().limit(-2).count();
    }

    @Test
    public void testLimitZero() {
        final Stream<Integer> stream = IntStream.range(0, 10).boxed().limit(0);
        assertThat(stream, isEmpty());
    }

    @Test
    public void testLimitMoreThanCount() {
        IntStream.range(0, 5).boxed().limit(15).chain(assertElements(contains(0, 1, 2, 3, 4)));
    }
}
