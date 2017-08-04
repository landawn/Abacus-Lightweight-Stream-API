package com.annimon.stream.streamtests;

import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.annimon.stream.Functions;
import com.annimon.stream.IntStream;

public final class ToArrayTest {

    @Test
    public void testToArray() {
        Object[] objects = IntStream.range(0, 200).boxed().filter(Functions.remainder(4)).toArray();

        assertEquals(50, objects.length);
        assertNotNull(objects[10]);
        assertThat(objects[0], instanceOf(Integer.class));
    }

    @Test
    public void testToArrayWithGenerator() {
        Integer[] numbers = IntStream.range(1, 1000).boxed().filter(Functions.remainder(2)).toArray(Functions.arrayGenerator(Integer[].class));

        assertTrue(numbers.length > 0);
        assertNotNull(numbers[100]);
    }
}
