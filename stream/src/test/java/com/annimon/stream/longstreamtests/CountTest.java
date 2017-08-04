package com.annimon.stream.longstreamtests;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.annimon.stream.LongStream;

public final class CountTest {

    @Test
    public void testCount() {
        assertThat(LongStream.of(100, 20, 3).count(), is(3));
        assertThat(LongStream.empty().count(), is(0));
    }
}
