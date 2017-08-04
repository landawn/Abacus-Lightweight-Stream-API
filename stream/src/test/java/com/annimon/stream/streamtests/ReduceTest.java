package com.annimon.stream.streamtests;

import static com.annimon.stream.test.hamcrest.OptionalMatcher.isEmpty;
import static com.annimon.stream.test.hamcrest.OptionalMatcher.isPresent;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.annimon.stream.Functions;
import com.annimon.stream.IntStream;
import com.annimon.stream.Optional;
import com.annimon.stream.Stream;
import com.annimon.stream.function.BiFunction;

public final class ReduceTest {

    @Test
    public void testReduceSumFromZero() {
        int result = IntStream.range(0, 10).boxed().reduce(0, Functions.addition());
        assertEquals(45, result);
    }

    @Test
    public void testReduceSumFromMinus45() {
        int result = IntStream.range(0, 10).boxed().reduce(-45, Functions.addition());
        assertEquals(0, result);
    }

    @Test
    public void testReduceWithAnotherType() {
        int result = Stream.of("a", "bb", "ccc", "dddd").reduce(0, new BiFunction<Integer, String, Integer>() {
            @Override
            public Integer apply(Integer length, String s) {
                return length + s.length();
            }
        });
        assertEquals(10, result);
    }

    @Test
    public void testReduceOptional() {
        Optional<Integer> result = IntStream.range(0, 10).boxed().reduce(Functions.addition());

        assertThat(result, isPresent());
        assertNotNull(result.get());
        assertEquals(45, (int) result.get());
    }

    @Test
    public void testReduceOptionalOnEmptyStream() {
        Optional<Integer> result = Stream.<Integer> empty().reduce(Functions.addition());

        assertThat(result, isEmpty());
        assertEquals(119, (int) result.orElse(119));
    }
}
