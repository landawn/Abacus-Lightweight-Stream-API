package com.annimon.stream.streamtests;

import org.junit.Test;

import com.annimon.stream.Functions;
import com.annimon.stream.Stream;
import com.annimon.stream.test.hamcrest.StreamMatcher;

public final class TakeUntilTest {

    //    @Test
    //    public void testTakeUntil() {
    //        Stream.of(2, 4, 6, 7, 8, 10, 11)
    //                .takeUntil(Predicate.Util.negate(Functions.remainder(2)))
    //                .chain(assertElements(contains(
    //                        2, 4, 6, 7
    //                )));
    //    }

    @Test
    public void testTakeUntilOnEmptyStream() {
        Stream.<Integer> empty().takeUntil(Functions.remainder(2)).chain(StreamMatcher.<Integer> assertIsEmpty());
    }
}
