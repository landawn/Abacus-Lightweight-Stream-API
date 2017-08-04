package com.annimon.stream.streamtests;

import static com.annimon.stream.test.hamcrest.StreamMatcher.assertElements;
import static org.hamcrest.Matchers.contains;

import org.junit.Test;

import com.annimon.stream.IntStream;
import com.annimon.stream.function.Function;

public final class WithoutNullsTest {

    @Test
    public void testWithoutNulls() {
        IntStream.range(0, 10).boxed().map(new Function<Integer, String>() {
            @Override
            public String apply(Integer integer) {
                return integer % 3 == 0 ? null : integer.toString();
            }
        }).skipNull().chain(assertElements(contains("1", "2", "4", "5", "7", "8")));
    }
}
