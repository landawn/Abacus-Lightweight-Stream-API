package com.annimon.stream.streamtests;

public final class FindIndexed {

    //    @Test
    //    public void testFindIndexed() {
    //        Indexed<Integer> result = Stream.rangeClosed(1, 10)
    //                .findIndexed(sumEquals(7))
    //                .get();
    //        assertThat(result.index(), is(3));
    //        assertThat(result.value(), is(4));
    //    }
    //
    //    @Test
    //    public void testFindIndexedWithStartAndStep() {
    //        Indexed<Integer> result = Stream.of(1, 11, 22, 12, 40)
    //                .findIndexed(0, 10, sumEquals(42))
    //                .get();
    //        assertThat(result.index(), is(20));
    //        assertThat(result.value(), is(22));
    //    }
    //
    //    @Test
    //    public void testFindIndexedNoMatch() {
    //        Optional<Indexed<Integer>> result = IntStream.range(0, 10).boxed()
    //                .findIndexed(sumEquals(42));
    //        assertThat(result, isEmpty());
    //    }
    //
    //
    //    private static IndexedPredicate<Integer> sumEquals(final int sum) {
    //        return new IndexedPredicate<Integer>() {
    //            @Override
    //            public boolean test(int index, Integer value) {
    //                return index + value == sum;
    //            }
    //        };
    //    }
}
