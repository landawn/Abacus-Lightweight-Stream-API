package com.annimon.stream.streamtests;

import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.annimon.stream.IntStream;

public final class ToListTest {

    @Test
    public void testToList() {
        assertThat(IntStream.range(0, 5).boxed().toList(), contains(0, 1, 2, 3, 4));
    }
}
