package com.annimon.stream.streamtests;

import static com.annimon.stream.test.hamcrest.StreamMatcher.assertElements;
import static org.hamcrest.Matchers.contains;

import org.junit.Test;

import com.annimon.stream.Functions;
import com.annimon.stream.IntStream;
import com.annimon.stream.Stream;
import com.annimon.stream.function.Function;

public final class FlatMapTest {

    @Test
    public void testFlatMap() {
        IntStream.rangeClosed(2, 4).boxed().flatMap(new Function<Integer, Stream<String>>() {

            @Override
            public Stream<String> apply(final Integer i) {
                return IntStream.rangeClosed(2, 4).boxed().filter(Functions.remainder(2)).map(new Function<Integer, String>() {

                    @Override
                    public String apply(Integer p) {
                        return String.format("%d * %d = %d", i, p, (i * p));
                    }
                });
            }
        }).chain(assertElements(contains("2 * 2 = 4", "2 * 4 = 8", "3 * 2 = 6", "3 * 4 = 12", "4 * 2 = 8", "4 * 4 = 16")));
    }
}
