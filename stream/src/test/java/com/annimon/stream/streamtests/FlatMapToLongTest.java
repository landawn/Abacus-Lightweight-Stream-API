package com.annimon.stream.streamtests;

public final class FlatMapToLongTest {

    //    @Test
    //    public void testFlatMapToLong() {
    //        LongStream.rangeClosed(2L, 4L).boxed().flatMapToLong(new Function<Long, LongStream>() {
    //            @Override
    //            public LongStream apply(Long t) {
    //                return LongStream.iterate(t, LongUnaryOperator.Util.identity()).limit(t);
    //            }
    //        }).chain(assertElements(arrayContaining(2L, 2L, 3L, 3L, 3L, 4L, 4L, 4L, 4L)));
    //    }
}
