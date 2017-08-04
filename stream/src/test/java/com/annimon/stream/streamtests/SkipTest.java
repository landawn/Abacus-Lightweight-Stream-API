package com.annimon.stream.streamtests;

import static com.annimon.stream.test.hamcrest.StreamMatcher.assertElements;
import static org.hamcrest.Matchers.contains;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.annimon.stream.IntStream;
import com.annimon.stream.Stream;
import com.annimon.stream.test.hamcrest.StreamMatcher;

public final class SkipTest {

    @Test
    public void testSkip() {
        IntStream.range(0, 10).boxed().skip(7).chain(assertElements(contains(7, 8, 9)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSkipNegative() {
        IntStream.range(0, 10).boxed().skip(-2).count();
    }

    @Test
    public void testSkipZero() {
        IntStream.range(0, 2).boxed().skip(0).chain(assertElements(contains(0, 1)));
    }

    @Test
    public void testSkipMoreThanCount() {
        IntStream.range(0, 10).boxed().skip(15).chain(StreamMatcher.<Integer> assertIsEmpty());
    }

    @Test
    public void testSkipLazy() {
        final List<Integer> data = new ArrayList<>(10);
        data.add(0);

        Stream<Integer> stream = Stream.of(data).skip(3);
        data.addAll(Arrays.asList(1, 2, 3, 4, 5));
        stream.chain(assertElements(contains(3, 4, 5)));
    }

    @Test
    public void testSkipAndLimit() {
        IntStream.range(0, 10).boxed().skip(2) // 23456789
                .limit(5) // 23456
                .chain(assertElements(contains(2, 3, 4, 5, 6)));
    }

    @Test
    public void testLimitAndSkip() {
        IntStream.range(0, 10).boxed().limit(5) // 01234
                .skip(2) // 234
                .chain(assertElements(contains(2, 3, 4)));
    }

    @Test
    public void testSkipAndLimitMoreThanCount() {
        IntStream.range(0, 10).boxed().skip(8) // 89
                .limit(15) // 89
                .chain(assertElements(contains(8, 9)));
    }

    @Test
    public void testSkipMoreThanCountAndLimit() {
        IntStream.range(0, 10).boxed().skip(15).limit(8).chain(StreamMatcher.<Integer> assertIsEmpty());
    }

    @Test
    public void testSkipAndLimitTwice() {
        IntStream.range(0, 10).boxed().skip(2) // 23456789
                .limit(5) // 23456
                .skip(2) // 456
                .limit(2) // 45
                .chain(assertElements(contains(4, 5)));
    }
}
