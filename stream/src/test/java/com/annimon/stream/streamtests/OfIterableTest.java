package com.annimon.stream.streamtests;

public final class OfIterableTest {

    //    @Test
    //    public void testStreamOfIterable() {
    //        Iterable<Integer> iterable = new Iterable<Integer>() {
    //            @Override
    //            public Iterator<Integer> iterator() {
    //                return Functions.counterIterator();
    //            }
    //        };
    //
    //        Stream.of(iterable)
    //                .limit(5)
    //                .chain(assertElements(contains(
    //                      0, 1, 2, 3, 4
    //                )));
    //    }
    //
    //    @Test(expected = NullPointerException.class)
    //    public void testStreamOfIterableNull() {
    //        Stream.of((Iterable<?>)null);
    //    }
}
