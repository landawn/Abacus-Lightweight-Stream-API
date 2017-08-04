package com.annimon.stream.streamtests;

public final class DropWhileIndexedTest {

    //    @Test
    //    public void testDropWhileIndexed() {
    //        Stream.of(1, 2, 3, 4, 0, 1, 2)
    //                .dropWhileIndexed(new IndexedPredicate<Integer>() {
    //                    @Override
    //                    public boolean test(int index, Integer value) {
    //                        return (index + value) < 5;
    //                    }
    //                })
    //                .chain(assertElements(contains(
    //                        3, 4, 0, 1, 2
    //                )));
    //    }
    //
    //    @Test
    //    public void testDropWhileIndexedWithStartAndStep() {
    //        Stream.of(1, 2, 3, 4, -5, -6, -7)
    //                .dropWhileIndexed(2, 2, new IndexedPredicate<Integer>() {
    //                    @Override
    //                    public boolean test(int index, Integer value) {
    //                        return (index + value) < 10;
    //                    }
    //                })
    //                .chain(assertElements(contains(
    //                        4, -5, -6, -7
    //                )));
    //    }
}
