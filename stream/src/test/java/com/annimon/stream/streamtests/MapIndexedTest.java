package com.annimon.stream.streamtests;

public final class MapIndexedTest {

    //    @Test
    //    public void testMapIndexed() {
    //        Stream.rangeClosed(4, 8)
    //                .mapIndexed(new IndexedFunction<Integer, Integer>() {
    //                    @Override
    //                    public Integer apply(int index, Integer t) {
    //                        return index * t;
    //                    }
    //                })
    //                .chain(assertElements(contains(
    //                       0,  // (0 * 4)
    //                       5,  // (1 * 5)
    //                       12, // (2 * 6)
    //                       21, // (3 * 7)
    //                       32  // (4 * 8)
    //                )));
    //    }
    //
    //    @Test
    //    public void testMapIndexedWithStartAndStep() {
    //        Stream.rangeClosed(4, 8)
    //                .mapIndexed(20, -5, new IndexedFunction<Integer, Integer>() {
    //                    @Override
    //                    public Integer apply(int index, Integer t) {
    //                        return index * t;
    //                    }
    //                })
    //                .chain(assertElements(contains(
    //                       80, // (20 * 4)
    //                       75, // (15 * 5)
    //                       60, // (10 * 6)
    //                       35, // (5  * 7)
    //                       0   // (0  * 8)
    //                )));
    //    }
}
