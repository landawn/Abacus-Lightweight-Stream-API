package com.annimon.stream.streamtests;

import static com.annimon.stream.test.hamcrest.StreamMatcher.elements;
import static com.annimon.stream.test.hamcrest.StreamMatcher.isEmpty;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.annimon.stream.Stream;

public final class OfNullableTest {

    @Test
    public void testStreamOfNullable() {
        assertThat(Stream.ofNullable(null), isEmpty());

        assertThat(Stream.ofNullable(5), elements(contains(5)));
    }

    //    @Test
    //    public void testStreamOfNullableWithIterable() {
    //        assertThat(Stream.ofNullable((List<?>) null), isEmpty());
    //
    //        assertThat(Stream.ofNullable(Arrays.asList(5, 10, 15)),
    //                elements(contains(5, 10, 15)));
    //    }
}
