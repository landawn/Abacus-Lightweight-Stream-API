package com.annimon.stream.streamtests;

import com.annimon.stream.Stream;
import java.util.Arrays;
import org.junit.Test;
import static com.annimon.stream.test.hamcrest.StreamMatcher.assertElements;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertEquals;

public final class SlidingWindowTest {

    @Test
    @SuppressWarnings("unchecked")
    public void testSlidingWindow() {
        long count = Stream.<Integer>empty().sliding(5, 6).count();
        assertEquals(0, count);

        Stream.of(                    1, 1, 1, 2, 2, 2, 3, 3, 3)
                .sliding(3, 3)
                .chain(assertElements(contains(
                        Arrays.asList(1, 1, 1),
                        Arrays.asList(         2, 2, 2),
                        Arrays.asList(                  3, 3, 3)
                )));

        Stream.of(                    1, 2, 3, 1, 2, 3, 1, 2, 3)
                .sliding(2, 3)
                .chain(assertElements(contains(
                        Arrays.asList(1, 2),
                        Arrays.asList(         1, 2),
                        Arrays.asList(                  1, 2)
                )));

        Stream.of(                    1, 2, 3, 4, 5, 6)
                .sliding(3, 1)
                .chain(assertElements(contains(
                        Arrays.asList(1, 2, 3),
                        Arrays.asList(   2, 3, 4),
                        Arrays.asList(      3, 4, 5),
                        Arrays.asList(         4, 5, 6)
                )));

        Stream.of(                    1, 2, 3, 4, 5, 6)
                .sliding(3)
                .chain(assertElements(contains(
                        Arrays.asList(1, 2, 3),
                        Arrays.asList(   2, 3, 4),
                        Arrays.asList(      3, 4, 5),
                        Arrays.asList(         4, 5, 6)
                )));

        Stream.of(                    1, 2)
                .sliding(3, 1)
                .chain(assertElements(contains(
                        Arrays.asList(1, 2)
                )));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSlidingWindowWithNegativeWindowSize() {
        Stream.of(1, 2, 3, 4).sliding(-1, 1).count();
    }

    @Test(expected = IllegalArgumentException.class, timeout=1000)
    public void testSlidingWindowWithNegativeStepSize() {
        Stream.of(1, 2, 3, 4).sliding(5, -1).count();
    }
}
