package com.annimon.stream.streamtests;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.annimon.stream.Functions;
import com.annimon.stream.Stream;

public final class IndexedTest {

    @Test
    public void testIndexed() {
        int[] expectedIndices = new int[] { 0, 1, 2, 3 };
        int[] actualIndices = Stream.of("a", "b", "c", "d").indexed().mapToInt(Functions.<String> intPairIndex()).toArray();
        assertThat(actualIndices, is(expectedIndices));
    }

    //    @Test
    //    public void testIndexedCustomStep() {
    //        int[] expectedIndices = new int[] {-10, -15, -20, -25};
    //        int[] actualIndices = Stream.of("a", "b", "c", "d")
    //                .indexed(-10, -5)
    //                .mapToInt(Functions.<String>intPairIndex())
    //                .toArray();
    //        assertThat(actualIndices, is(expectedIndices));
    //    }
    //
    //    @Test
    //    public void testIndexedReverse() {
    //        Stream.of("first", "second", "third", "fourth", "fifth")
    //                .indexed(0, -1)
    //                .sortBy(new Function<Indexed<String>, Integer>() {
    //                    @Override
    //                    public Integer apply(Indexed<String> t) {
    //                        return t.index();
    //                    }
    //                })
    //                .map(new Function<Indexed<String>, String>() {
    //
    //                    @Override
    //                    public String apply(Indexed<String> t) {
    //                        return t.value();
    //                    }
    //                })
    //                .chain(assertElements(contains(
    //                        "fifth", "fourth", "third", "second", "first"
    //                )));
    //    }
}
