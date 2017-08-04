package com.annimon.stream.longstreamtests;

import com.annimon.stream.LongStream;
import com.landawn.abacus.util.function.LongToIntFunction;
import org.junit.Test;
import static com.annimon.stream.test.hamcrest.IntStreamMatcher.assertElements;
import static org.hamcrest.Matchers.arrayContaining;

public final class MapToIntTest {

    @Test
    public void testMapToInt() {
        LongToIntFunction mapper = new LongToIntFunction() {
            @Override
            public int applyAsInt(long value) {
                return (int) (value / 10);
            }
        };
        LongStream.of(10L, 20L, 30L, 40L)
                .mapToInt(mapper)
                .chain(assertElements(arrayContaining(
                        1, 2, 3, 4
                )));
    }
}
