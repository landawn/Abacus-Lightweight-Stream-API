package com.annimon.stream.doublestreamtests;

import com.annimon.stream.DoubleStream;
import com.landawn.abacus.util.function.DoubleFunction;
import java.util.NoSuchElementException;
import org.junit.Test;
import static com.annimon.stream.test.hamcrest.DoubleStreamMatcher.assertElements;
import static com.annimon.stream.test.hamcrest.DoubleStreamMatcher.elements;
import static org.hamcrest.Matchers.arrayContaining;
import static org.junit.Assert.assertThat;

public final class FlatMapTest {

    @Test
    public void testFlatMap() {
        DoubleFunction<DoubleStream> twicer = new DoubleFunction<DoubleStream>() {
            @Override
            public DoubleStream apply(double value) {
                return DoubleStream.of(value, value);
            }
        };
        DoubleStream.of(0.012, -3.039, 100d)
                .flatMap(twicer)
                .chain(assertElements(arrayContaining(
                        0.012, 0.012, -3.039, -3.039, 100d, 100d
                )));

        DoubleStream.of(0.012, -3.039, 100d)
                .flatMap(new DoubleFunction<DoubleStream>() {
                    @Override
                    public DoubleStream apply(double value) {
                        if (value < 0) return DoubleStream.of(value);
                        return null;
                    }
                })
                .chain(assertElements(arrayContaining(
                        -3.039
                )));

        DoubleStream.of(0.012, -3.039, 100d)
                .flatMap(new DoubleFunction<DoubleStream>() {
                    @Override
                    public DoubleStream apply(double value) {
                        if (value < 0) return DoubleStream.empty();
                        return DoubleStream.of(value);
                    }
                })
                .chain(assertElements(arrayContaining(
                        0.012, 100d
                )));
    }

    @Test(expected = NoSuchElementException.class)
    public void testFlatMapIterator() {
        DoubleStream.empty().flatMap(new DoubleFunction<DoubleStream>() {
            @Override
            public DoubleStream apply(double value) {
                return DoubleStream.of(value);
            }
        }).iterator().nextDouble();
    }
}
