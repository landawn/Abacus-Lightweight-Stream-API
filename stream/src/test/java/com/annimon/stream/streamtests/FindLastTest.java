package com.annimon.stream.streamtests;

import static com.annimon.stream.test.hamcrest.OptionalMatcher.isPresent;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.annimon.stream.Functions;
import com.annimon.stream.IntStream;
import com.annimon.stream.Optional;
import com.annimon.stream.Stream;
import com.annimon.stream.test.hamcrest.OptionalMatcher;

public final class FindLastTest {

    @Test
    public void testFindLast() {
        Optional<Integer> result = IntStream.rangeClosed(0, 10).boxed().findLast();
        assertThat(result, isPresent());
        assertNotNull(result.get());
        assertEquals(10, (int) result.get());
    }

    @Test
    public void testFindLastOnEmptyStream() {
        assertThat(Stream.empty().findLast(), OptionalMatcher.isEmpty());
    }

    @Test
    public void testFindLastAfterFiltering() {
        Optional<Integer> result = IntStream.range(1, 100).boxed().filter(Functions.remainder(6)).findLast();

        assertThat(result, isPresent());
        assertNotNull(result.get());
        assertEquals(96, (int) result.get());
    }
}
