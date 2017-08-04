package com.annimon.stream.streamtests;

public final class TakeWhileIndexedTest {

    //    @Test
    //    public void testTakeWhileIndexed() {
    //        Stream.of(1, 2, 3,  4, -5, -6, -7)
    //                .takeWhileIndexed(new IndexedPredicate<Integer>() {
    //                    @Override
    //                    public boolean test(int index, Integer value) {
    //                        return index + value < 5;
    //                    }
    //                })
    //                .chain(assertElements(contains(
    //                        1, 2
    //                )));
    //    }
    //
    //    @Test
    //    public void testTakeWhileIndexedWithStartAndStep() {
    //        Stream.of(1, 2, 3,  4, -5, -6, -7)
    //                .takeWhileIndexed(2, 2, new IndexedPredicate<Integer>() {
    //                    @Override
    //                    public boolean test(int index, Integer value) {
    //                        return index + value < 8;
    //                    }
    //                })
    //                .chain(assertElements(contains(
    //                        1, 2
    //                )));
    //    }
}
