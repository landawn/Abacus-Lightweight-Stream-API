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

public final class FindFirstTest {

    @Test
    public void testFindFirst() {
        Optional<Integer> result = IntStream.range(0, 10).boxed().findFirst();
        assertThat(result, isPresent());
        assertNotNull(result.get());
        assertEquals(0, (int) result.get());
    }

    @Test
    public void testFindFirstOnEmptyStream() {
        assertThat(Stream.empty().findFirst(), OptionalMatcher.isEmpty());
    }

    @Test
    public void testFindFirstAfterFiltering() {
        Optional<Integer> result = IntStream.range(1, 1000).boxed().filter(Functions.remainder(6)).findFirst();

        assertThat(result, isPresent());
        assertNotNull(result.get());
        assertEquals(6, (int) result.get());
    }
}
