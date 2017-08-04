package com.annimon.stream.streamtests;

import static com.annimon.stream.test.hamcrest.StreamMatcher.assertElements;
import static org.hamcrest.Matchers.contains;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.annimon.stream.Functions;
import com.annimon.stream.IntStream;
import com.annimon.stream.Stream;
import com.annimon.stream.function.Function;

public final class ConcatTest {

    @Test
    public void testConcat() {
        Stream<String> stream1 = Stream.of("a", "b", "c", "d");
        Stream<String> stream2 = Stream.of("e", "f", "g", "h");
        Stream.concat(stream1, stream2).chain(assertElements(contains("a", "b", "c", "d", "e", "f", "g", "h")));
    }

    @Test(expected = NullPointerException.class)
    public void testConcatNull1() {
        Stream.concat(null, Stream.empty());
    }

    @Test(expected = NullPointerException.class)
    public void testConcatNull2() {
        Stream.concat(Stream.empty(), null);
    }

    @Test
    public void testConcatOfFilter() {
        Stream<Integer> stream1 = IntStream.range(0, 5).boxed().filter(Functions.remainder(1));
        Stream<Integer> stream2 = IntStream.range(5, 10).boxed().filter(Functions.remainder(1));
        Stream.concat(stream1, stream2).chain(assertElements(contains(0, 1, 2, 3, 4, 5, 6, 7, 8, 9)));
    }

    @Test
    public void testConcatOfFlatMap() {
        final Function<Integer, Stream<Integer>> flatmapFunc = new Function<Integer, Stream<Integer>>() {
            @Override
            public Stream<Integer> apply(Integer value) {
                return Stream.of(value, value);
            }
        };
        Stream<Integer> stream1 = IntStream.range(1, 3).boxed().flatMap(flatmapFunc); // 1122
        Stream<Integer> stream2 = IntStream.range(3, 5).boxed().flatMap(flatmapFunc); // 3344
        Stream.concat(stream1, stream2).chain(assertElements(contains(1, 1, 2, 2, 3, 3, 4, 4)));
    }

    @Test
    public void testMergeIterator() {
        List<Integer> shorter = Arrays.asList(1, 2, 3, 4, 5);
        List<Integer> longer = IntStream.rangeClosed(2, 8).boxed().toList();
        Stream.concat(shorter.iterator(), longer.iterator()).chain(assertElements(contains(1, 2, 3, 4, 5, 2, 3, 4, 5, 6, 7, 8)));
        Stream.concat(longer.iterator(), shorter.iterator()).chain(assertElements(contains(2, 3, 4, 5, 6, 7, 8, 1, 2, 3, 4, 5)));
    }
}
