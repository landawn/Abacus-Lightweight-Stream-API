package com.annimon.stream.streamtests;

public final class TakeUntilIndexedTest {
    //    
    //    @Test
    //    public void testTakeUntilIndexed() {
    //        Stream.of(1, 2, 3, 4, 0, 1, 2)
    //                .takeUntilIndexed(new IndexedPredicate<Integer>() {
    //                    @Override
    //                    public boolean test(int index, Integer value) {
    //                        return (index + value) > 4;
    //                    }
    //                })
    //                .chain(assertElements(contains(
    //                        1, 2, 3
    //                )));
    //    }
    //
    //    @Test
    //    public void testTakeUntilIndexedWithStartAndStep() {
    //        Stream.of(1, 2, 3, 4, 0, 1, 2)
    //                .takeUntilIndexed(2, 2, new IndexedPredicate<Integer>() {
    //                    @Override
    //                    public boolean test(int index, Integer value) {
    //                        return (index + value) > 8;
    //                    }
    //                })
    //                .chain(assertElements(contains(
    //                        1, 2, 3
    //                )));
    //    }
}
