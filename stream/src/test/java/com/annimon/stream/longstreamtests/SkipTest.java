package com.annimon.stream.longstreamtests;

import com.annimon.stream.LongStream;
import org.junit.Test;
import static com.annimon.stream.test.hamcrest.LongStreamMatcher.assertElements;
import static com.annimon.stream.test.hamcrest.LongStreamMatcher.assertIsEmpty;
import static org.hamcrest.Matchers.arrayContaining;

public final class SkipTest {

    @Test
    public void testSkip() {
        LongStream.of(12L, 32L, 22L)
                .skip(2)
                .chain(assertElements(arrayContaining(
                        22L
                )));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSkipNegative() {
        LongStream.of(12L, 32L).skip(-2).count();
    }

    @Test
    public void testSkipZero() {
        LongStream.of(12L, 32L, 22L)
                .skip(0)
                .chain(assertElements(arrayContaining(
                        12L, 32L, 22L
                )));
    }

    @Test
    public void testSkipMoreThanCount() {
        LongStream.of(12L, 32L, 22L)
                .skip(5)
                .chain(assertIsEmpty());
    }
}
