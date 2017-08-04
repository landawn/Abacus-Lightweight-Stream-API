package com.annimon.stream.streamtests;

public final class ForEachIndexedTest {

    //    @Test
    //    @SuppressWarnings("unchecked")
    //    public void testForEachIndexed() {
    //        final List<Indexed<String>> result = new ArrayList<Indexed<String>>();
    //        Stream.of("a", "b", "c")
    //                .forEachIndexed(new IndexedConsumer<String>() {
    //                    @Override
    //                    public void accept(int index, String t) {
    //                        result.add(new Indexed<String>(index, t));
    //                    }
    //                });
    //        assertThat(result, is(Arrays.asList(
    //                new Indexed<String>(0, "a"),
    //                new Indexed<String>(1, "b"),
    //                new Indexed<String>(2, "c")
    //        )));
    //    }
    //
    //    @Test
    //    @SuppressWarnings("unchecked")
    //    public void testForEachIndexedWithStartAndStep() {
    //        final List<Indexed<String>> result = new ArrayList<Indexed<String>>();
    //        Stream.of("a", "b", "c")
    //                .forEachIndexed(50, -10, new IndexedConsumer<String>() {
    //                    @Override
    //                    public void accept(int index, String t) {
    //                        result.add(new Indexed<String>(index, t));
    //                    }
    //                });
    //        assertThat(result, is(Arrays.asList(
    //                new Indexed<String>(50, "a"),
    //                new Indexed<String>(40, "b"),
    //                new Indexed<String>(30, "c")
    //        )));
    //    }
}
