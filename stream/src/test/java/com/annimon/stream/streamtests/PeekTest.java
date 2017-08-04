package com.annimon.stream.streamtests;

import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.annimon.stream.IntStream;
import com.landawn.abacus.util.function.Consumer;

public final class PeekTest {

    @Test
    public void testPeek() {
        final List<Integer> result = new ArrayList<>();
        long count = IntStream.range(0, 5).boxed().peek(new Consumer<Integer>() {
            @Override
            public void accept(Integer t) {
                result.add(t);
            }
        }).count();
        assertEquals(5, count);
        assertThat(result, contains(0, 1, 2, 3, 4));
    }
}
