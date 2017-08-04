package com.annimon.stream.intstreamtests;

import com.annimon.stream.IntStream;
import com.landawn.abacus.util.function.IntConsumer;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public final class ForEachTest {

    @Test
    public void testForEach() {
        IntStream.empty().forEach(new IntConsumer() {
            @Override
            public void accept(int value) {
                throw new IllegalStateException();
            }
        });

        IntStream.of(42).forEach(new IntConsumer() {
            @Override
            public void accept(int value) {
                assertTrue(value == 42);
            }
        });

        final int[] sum = new int[1];

        IntStream.rangeClosed(10, 20).forEach(new IntConsumer() {
            @Override
            public void accept(int value) {
                sum[0] += value;
            }
        });

        assertEquals(sum[0], 165);
    }
}
