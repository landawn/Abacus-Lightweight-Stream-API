package com.annimon.stream.streamtests;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.annimon.stream.Stream;

public final class IteratorTest {

    @Test
    @SuppressWarnings("deprecation")
    public void testGetIterator() {
        assertThat(Stream.of(1).iterator(), is(not(nullValue())));
    }

    @Test
    public void testIterator() {
        assertThat(Stream.of(1).iterator(), is(not(nullValue())));
    }
}
