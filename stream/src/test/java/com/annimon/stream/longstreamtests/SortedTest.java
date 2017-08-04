package com.annimon.stream.longstreamtests;

import static com.annimon.stream.test.hamcrest.LongStreamMatcher.assertElements;
import static com.annimon.stream.test.hamcrest.LongStreamMatcher.assertIsEmpty;
import static org.hamcrest.Matchers.arrayContaining;

import java.util.Comparator;

import org.junit.Test;

import com.annimon.stream.LongStream;
import com.annimon.stream.Objects;

public final class SortedTest {

    @Test
    public void testSorted() {
        LongStream.of(12, 32, 9, 22).sorted().chain(assertElements(arrayContaining(9L, 12L, 22L, 32L)));

        LongStream.empty().sorted().chain(assertIsEmpty());
    }

    @Test
    public void testSortedWithComparator() {
        LongStream.of(12, 32, 9, 22).sorted(new Comparator<Long>() {
            @Override
            public int compare(Long o1, Long o2) {
                // reverse order
                return Objects.compare(o2, o1);
            }
        }).chain(assertElements(arrayContaining(32L, 22L, 12L, 9L)));
    }
}
