package com.annimon.stream;

import java.io.Closeable;
import java.math.BigInteger;
import java.util.Comparator;
import java.util.NoSuchElementException;

import com.annimon.stream.internal.Compose;
import com.annimon.stream.internal.Operators;
import com.annimon.stream.internal.Params;
import com.annimon.stream.iterator.PrimitiveIterator;
import com.annimon.stream.iterator.PrimitiveIterator.OfLong;
import com.annimon.stream.operator.LongArray;
import com.annimon.stream.operator.LongConcat;
import com.annimon.stream.operator.LongDropWhile;
import com.annimon.stream.operator.LongFilter;
import com.annimon.stream.operator.LongFlatMap;
import com.annimon.stream.operator.LongGenerate;
import com.annimon.stream.operator.LongIterate;
import com.annimon.stream.operator.LongLimit;
import com.annimon.stream.operator.LongMap;
import com.annimon.stream.operator.LongMapToDouble;
import com.annimon.stream.operator.LongMapToInt;
import com.annimon.stream.operator.LongMapToObj;
import com.annimon.stream.operator.LongPeek;
import com.annimon.stream.operator.LongRangeClosed;
import com.annimon.stream.operator.LongScan;
import com.annimon.stream.operator.LongScanIdentity;
import com.annimon.stream.operator.LongSkip;
import com.annimon.stream.operator.LongSorted;
import com.annimon.stream.operator.LongTakeUntil;
import com.annimon.stream.operator.LongTakeWhile;
import com.landawn.abacus.util.N;
import com.landawn.abacus.util.OptionalDouble;
import com.landawn.abacus.util.OptionalLong;
import com.landawn.abacus.util.function.Function;
import com.landawn.abacus.util.function.LongBinaryOperator;
import com.landawn.abacus.util.function.LongConsumer;
import com.landawn.abacus.util.function.LongFunction;
import com.landawn.abacus.util.function.LongPredicate;
import com.landawn.abacus.util.function.LongSupplier;
import com.landawn.abacus.util.function.LongToDoubleFunction;
import com.landawn.abacus.util.function.LongToIntFunction;
import com.landawn.abacus.util.function.LongUnaryOperator;
import com.landawn.abacus.util.function.ObjLongConsumer;
import com.landawn.abacus.util.function.Supplier;
import com.landawn.abacus.util.function.ToLongFunction;

/**
 * A sequence of {@code long}-valued elements supporting aggregate operations.
 *
 * @since 1.1.4
 * @see Stream
 */
public final class LongStream implements Closeable {

    /**
     * Single instance for empty stream. It is safe for multi-thread environment because it has no content.
     */
    private static final LongStream EMPTY = new LongStream(new PrimitiveIterator.OfLong() {

        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public long nextLong() {
            return 0L;
        }
    });

    /**
     * Returns an empty stream.
     *
     * @return the empty stream
     */
    public static LongStream empty() {
        return EMPTY;
    }

    /**
     * Creates a {@code LongStream} from the specified values.
     *
     * @param values  the elements of the new stream
     * @return the new stream
     * @throws NullPointerException if {@code values} is null
     */
    public static LongStream of(final long... values) {
        if (values == null || values.length == 0) {
            return LongStream.empty();
        }

        return new LongStream(new LongArray(values));
    }

    /**
     * Creates a {@code LongStream} from {@code PrimitiveIterator.OfLong}.
     *
     * @param iterator  the iterator with elements to be passed to stream
     * @return the new {@code LongStream}
     * @throws NullPointerException if {@code iterator} is null
     */
    public static LongStream of(PrimitiveIterator.OfLong iterator) {
        N.requireNonNull(iterator);
        return new LongStream(iterator);
    }

    /**
     * Returns a sequential ordered {@code LongStream} from {@code startInclusive}
     * (inclusive) to {@code endExclusive} (exclusive) by an incremental step of
     * {@code 1}.
     *
     * @param startInclusive the (inclusive) initial value
     * @param endExclusive the exclusive upper bound
     * @return a sequential {@code LongStream} for the range of {@code long}
     *         elements
     */
    public static LongStream range(final long startInclusive, final long endExclusive) {
        if (startInclusive >= endExclusive) {
            return empty();
        }
        return rangeClosed(startInclusive, endExclusive - 1);
    }

    public static LongStream range(final long startInclusive, final long endExclusive, final long by) {
        if (by == 0) {
            throw new IllegalArgumentException("'by' can't be zero");
        }

        if (endExclusive == startInclusive || endExclusive > startInclusive != by > 0) {
            return empty();
        }

        if ((by > 0 && endExclusive - startInclusive < 0) || (by < 0 && startInclusive - endExclusive < 0)) {
            long m = BigInteger.valueOf(endExclusive).subtract(BigInteger.valueOf(startInclusive)).divide(BigInteger.valueOf(3)).longValue();

            if ((by > 0 && by > m) || (by < 0 && by < m)) {
                return concat(range(startInclusive, startInclusive + by), range(startInclusive + by, endExclusive));
            } else {
                m = m > 0 ? m - m % by : m + m % by;
                return concat(concat(range(startInclusive, startInclusive + m, by), range(startInclusive + m, (startInclusive + m) + m, by)),
                        range((startInclusive + m) + m, endExclusive, by));
            }
        }

        return of(new PrimitiveIterator.OfLong() {
            private long next = startInclusive;
            private long cnt = (endExclusive - startInclusive) / by + ((endExclusive - startInclusive) % by == 0 ? 0 : 1);

            @Override
            public boolean hasNext() {
                return cnt > 0;
            }

            @Override
            public long nextLong() {
                if (cnt-- <= 0) {
                    throw new NoSuchElementException();
                }

                long result = next;
                next += by;
                return result;
            }
        });
    }

    /**
     * Returns a sequential ordered {@code LongStream} from {@code startInclusive}
     * (inclusive) to {@code endInclusive} (inclusive) by an incremental step of
     * {@code 1}.
     *
     * @param startInclusive the (inclusive) initial value
     * @param endInclusive the inclusive upper bound
     * @return a sequential {@code LongStream} for the range of {@code long}
     *         elements
     */
    public static LongStream rangeClosed(final long startInclusive, final long endInclusive) {
        if (startInclusive > endInclusive) {
            return empty();
        } else if (startInclusive == endInclusive) {
            return of(startInclusive);
        } else
            return new LongStream(new LongRangeClosed(startInclusive, endInclusive));
    }

    public static LongStream rangeClosed(final long startInclusive, final long endInclusive, final long by) {
        if (by == 0) {
            throw new IllegalArgumentException("'by' can't be zero");
        }

        if (endInclusive == startInclusive) {
            return of(startInclusive);
        } else if (endInclusive > startInclusive != by > 0) {
            return empty();
        }

        if ((by > 0 && endInclusive - startInclusive < 0) || (by < 0 && startInclusive - endInclusive < 0) || ((endInclusive - startInclusive) / by + 1 <= 0)) {
            long m = BigInteger.valueOf(endInclusive).subtract(BigInteger.valueOf(startInclusive)).divide(BigInteger.valueOf(3)).longValue();

            if ((by > 0 && by > m) || (by < 0 && by < m)) {
                return concat(range(startInclusive, startInclusive + by), rangeClosed(startInclusive + by, endInclusive));
            } else {
                m = m > 0 ? m - m % by : m + m % by;
                return concat(concat(range(startInclusive, startInclusive + m, by), range(startInclusive + m, (startInclusive + m) + m, by)),
                        rangeClosed((startInclusive + m) + m, endInclusive, by));
            }
        }

        return of(new PrimitiveIterator.OfLong() {
            private long next = startInclusive;
            private long cnt = (endInclusive - startInclusive) / by + 1;

            @Override
            public boolean hasNext() {
                return cnt > 0;
            }

            @Override
            public long nextLong() {
                if (cnt-- <= 0) {
                    throw new NoSuchElementException();
                }

                long result = next;
                next += by;
                return result;
            }
        });
    }

    /**
     * Creates a {@code LongStream} by elements that generated by {@code LongSupplier}.
     *
     * @param s  the {@code LongSupplier} for generated elements
     * @return a new infinite sequential {@code LongStream}
     * @throws NullPointerException if {@code s} is null
     */
    public static LongStream generate(final LongSupplier s) {
        N.requireNonNull(s);
        return new LongStream(new LongGenerate(s));
    }

    /**
     * Creates a {@code LongStream} by iterative application {@code LongUnaryOperator} function
     * to an initial element {@code seed}. Produces {@code LongStream} consisting of
     * {@code seed}, {@code f(seed)}, {@code f(f(seed))}, etc.
     *
     * <p> The first element (position {@code 0}) in the {@code LongStream} will be
     * the provided {@code seed}. For {@code n > 0}, the element at position
     * {@code n}, will be the result of applying the function {@code f} to the
     * element at position {@code n - 1}.
     *
     * <p>Example:
     * <pre>
     * seed: 1
     * f: (a) -&gt; a + 5
     * result: [1, 6, 11, 16, ...]
     * </pre>
     *
     * @param seed the initial element
     * @param f  a function to be applied to the previous element to produce a new element
     * @return a new sequential {@code LongStream}
     * @throws NullPointerException if {@code f} is null
     */
    public static LongStream iterate(final long seed, final LongUnaryOperator f) {
        N.requireNonNull(f);
        return new LongStream(new LongIterate(seed, f));
    }

    /**
     * Creates an {@code LongStream} by iterative application {@code LongUnaryOperator} function
     * to an initial element {@code seed}, conditioned on satisfying the supplied predicate.
     *
     * <p>Example:
     * <pre>
     * seed: 0
     * predicate: (a) -&gt; a &lt; 20
     * f: (a) -&gt; a + 5
     * result: [0, 5, 10, 15]
     * </pre>
     *
     * @param seed  the initial value
     * @param predicate  a predicate to determine when the stream must terminate
     * @param op  operator to produce new element by previous one
     * @return the new stream
     * @throws NullPointerException if {@code op} is null
     * @since 1.1.5
     */
    public static LongStream iterate(final long seed, final LongPredicate predicate, final LongUnaryOperator op) {
        N.requireNonNull(predicate);
        return iterate(seed, op).takeWhile(predicate);
    }

    /**
     * Concatenates two streams.
     *
     * <p>Example:
     * <pre>
     * stream a: [1, 2, 3, 4]
     * stream b: [5, 6]
     * result:   [1, 2, 3, 4, 5, 6]
     * </pre>
     *
     * @param a  the first stream
     * @param b  the second stream
     * @return the new concatenated stream
     * @throws NullPointerException if {@code a} or {@code b} is null
     */
    public static LongStream concat(final LongStream a, final LongStream b) {
        N.requireNonNull(a);
        N.requireNonNull(b);
        @SuppressWarnings("resource")
        LongStream result = new LongStream(new LongConcat(a.iterator, b.iterator));
        return result.onClose(Compose.closeables(a, b));
    }

    public static LongStream concat(final long[] a, final long[] b) {
        return new LongStream(new LongConcat(OfLong.of(a), OfLong.of(b)));
    }

    private final PrimitiveIterator.OfLong iterator;
    private final Params params;

    private LongStream(PrimitiveIterator.OfLong iterator) {
        this(null, iterator);
    }

    LongStream(Params params, PrimitiveIterator.OfLong iterator) {
        this.params = params;
        this.iterator = iterator;
    }

    /**
     * Returns internal {@code LongStream} iterator.
     *
     * @return internal {@code LongStream} iterator.
     */
    public PrimitiveIterator.OfLong iterator() {
        return iterator;
    }

    /**
     * Returns a {@code Stream} consisting of the elements of this stream,
     * each boxed to an {@code Long}.
     *
     * <p>This is an lazy intermediate operation.
     *
     * @return a {@code Stream} consistent of the elements of this stream,
     *         each boxed to an {@code Long}
     */
    public Stream<Long> boxed() {
        return new Stream<>(params, iterator);
    }

    /**
     * Returns {@code LongStream} with elements that satisfy the given predicate.
     *
     * <p> This is an intermediate operation.
     *
     * <p>Example:
     * <pre>
     * predicate: (a) -&gt; a &gt; 2
     * stream: [1, 2, 3, 4, -8, 0, 11]
     * result: [3, 4, 11]
     * </pre>
     *
     * @param predicate  the predicate used to filter elements
     * @return the new stream
     */
    public LongStream filter(final LongPredicate predicate) {
        return new LongStream(params, new LongFilter(iterator, predicate));
    }

    /**
     * Returns an {@code LongStream} consisting of the results of applying the given
     * function to the elements of this stream.
     *
     * <p> This is an intermediate operation.
     *
     * <p>Example:
     * <pre>
     * mapper: (a) -&gt; a + 5
     * stream: [1, 2, 3, 4]
     * result: [6, 7, 8, 9]
     * </pre>
     *
     * @param mapper  the mapper function used to apply to each element
     * @return the new stream
     * @see Stream#map(com.landawn.abacus.util.function.Function)
     */
    public LongStream map(final LongUnaryOperator mapper) {
        return new LongStream(params, new LongMap(iterator, mapper));
    }

    /**
     * Returns a {@code Stream} consisting of the results of applying the given
     * function to the elements of this stream.
     *
     * <p> This is an intermediate operation.
     *
     * @param <R> the type result
     * @param mapper  the mapper function used to apply to each element
     * @return the new {@code Stream}
     */
    public <R> Stream<R> mapToObj(final LongFunction<? extends R> mapper) {
        return new Stream<>(params, new LongMapToObj<>(iterator, mapper));
    }

    /**
     * Returns an {@code IntStream} consisting of the results of applying the given
     * function to the elements of this stream.
     *
     * <p> This is an intermediate operation.
     *
     * @param mapper  the mapper function used to apply to each element
     * @return the new {@code IntStream}
     */
    public IntStream mapToInt(final LongToIntFunction mapper) {
        return new IntStream(params, new LongMapToInt(iterator, mapper));
    }

    /**
     * Returns an {@code DoubleStream} consisting of the results of applying the given
     * function to the elements of this stream.
     *
     * <p> This is an intermediate operation.
     *
     * @param mapper  the mapper function used to apply to each element
     * @return the new {@code DoubleStream}
     */
    public DoubleStream mapToDouble(final LongToDoubleFunction mapper) {
        return new DoubleStream(params, new LongMapToDouble(iterator, mapper));
    }

    /**
     * Returns a stream consisting of the results of replacing each element of
     * this stream with the contents of a mapped stream produced by applying
     * the provided mapping function to each element.
     *
     * <p>This is an intermediate operation.
     *
     * <p>Example:
     * <pre>
     * mapper: (a) -&gt; [a, a + 5]
     * stream: [1, 2, 3, 4]
     * result: [1, 6, 2, 7, 3, 8, 4, 9]
     * </pre>
     *
     * @param mapper  the mapper function used to apply to each element
     * @return the new stream
     * @see Stream#flatMap(com.landawn.abacus.util.function.Function)
     */
    public LongStream flatMap(final LongFunction<? extends LongStream> mapper) {
        return new LongStream(params, new LongFlatMap(iterator, mapper));
    }

    /**
     * Returns a stream consisting of the distinct elements of this stream.
     *
     * <p>This is a stateful intermediate operation.
     *
     * <p>Example:
     * <pre>
     * stream: [1, 4, 2, 3, 3, 4, 1]
     * result: [1, 4, 2, 3]
     * </pre>
     *
     * @return the new stream
     */
    public LongStream distinct() {
        return boxed().distinct().mapToLong(UNBOX_FUNCTION);
    }

    /**
     * Returns a stream consisting of the elements of this stream in sorted order.
     *
     * <p>This is a stateful intermediate operation.
     *
     * <p>Example:
     * <pre>
     * stream: [3, 4, 1, 2]
     * result: [1, 2, 3, 4]
     * </pre>
     *
     * @return the new stream
     */
    public LongStream sorted() {
        return new LongStream(params, new LongSorted(iterator));
    }

    /**
     * Returns a stream consisting of the elements of this stream
     * in sorted order as determinated by provided {@code Comparator}.
     *
     * <p>This is a stateful intermediate operation.
     *
     * <p>Example:
     * <pre>
     * comparator: (a, b) -&gt; -a.compareTo(b)
     * stream: [1, 2, 3, 4]
     * result: [4, 3, 2, 1]
     * </pre>
     *
     * @param comparator  the {@code Comparator} to compare elements
     * @return the new {@code LongStream}
     */
    public LongStream sorted(Comparator<Long> comparator) {
        return boxed().sorted(comparator).mapToLong(UNBOX_FUNCTION);
    }

    /**
     * Performs provided action on each element.
     *
     * <p>This is an intermediate operation.
     *
     * @param action the action to be performed on each element
     * @return the new stream
     */
    public LongStream peek(final LongConsumer action) {
        return new LongStream(params, new LongPeek(iterator, action));
    }

    /**
     * Returns a {@code LongStream} produced by iterative application of a accumulation function
     * to reduction value and next element of the current stream.
     * Produces a {@code LongStream} consisting of {@code value1}, {@code acc(value1, value2)},
     * {@code acc(acc(value1, value2), value3)}, etc.
     *
     * <p>This is an intermediate operation.
     *
     * <p>Example:
     * <pre>
     * accumulator: (a, b) -&gt; a + b
     * stream: [1, 2, 3, 4, 5]
     * result: [1, 3, 6, 10, 15]
     * </pre>
     *
     * @param accumulator  the accumulation function
     * @return the new stream
     * @throws NullPointerException if {@code accumulator} is null
     * @since 1.1.6
     */
    public LongStream scan(final LongBinaryOperator accumulator) {
        N.requireNonNull(accumulator);
        return new LongStream(params, new LongScan(iterator, accumulator));
    }

    /**
     * Returns a {@code LongStream} produced by iterative application of a accumulation function
     * to an initial element {@code identity} and next element of the current stream.
     * Produces a {@code LongStream} consisting of {@code identity}, {@code acc(identity, value1)},
     * {@code acc(acc(identity, value1), value2)}, etc.
     *
     * <p>This is an intermediate operation.
     *
     * <p>Example:
     * <pre>
     * identity: 0
     * accumulator: (a, b) -&gt; a + b
     * stream: [1, 2, 3, 4, 5]
     * result: [0, 1, 3, 6, 10, 15]
     * </pre>
     *
     * @param identity  the initial value
     * @param accumulator  the accumulation function
     * @return the new stream
     * @throws NullPointerException if {@code accumulator} is null
     * @since 1.1.6
     */
    public LongStream scan(final long identity, final LongBinaryOperator accumulator) {
        N.requireNonNull(accumulator);
        return new LongStream(params, new LongScanIdentity(iterator, identity, accumulator));
    }

    /**
     * Takes elements while the predicate returns {@code true}.
     *
     * <p>This is an intermediate operation.
     *
     * <p>Example:
     * <pre>
     * predicate: (a) -&gt; a &lt; 3
     * stream: [1, 2, 3, 4, 1, 2, 3, 4]
     * result: [1, 2]
     * </pre>
     *
     * @param predicate  the predicate used to take elements
     * @return the new {@code LongStream}
     */
    public LongStream takeWhile(final LongPredicate predicate) {
        return new LongStream(params, new LongTakeWhile(iterator, predicate));
    }

    /**
     * Takes elements while the predicate returns {@code false}.
     * Once predicate condition is satisfied by an element, the stream
     * finishes with this element.
     *
     * <p>This is an intermediate operation.
     *
     * <p>Example:
     * <pre>
     * stopPredicate: (a) -&gt; a &gt; 2
     * stream: [1, 2, 3, 4, 1, 2, 3, 4]
     * result: [1, 2, 3]
     * </pre>
     *
     * @param stopPredicate  the predicate used to take elements
     * @return the new {@code LongStream}
     * @since 1.1.6
     */
    public LongStream takeUntil(final LongPredicate stopPredicate) {
        return new LongStream(params, new LongTakeUntil(iterator, stopPredicate));
    }

    /**
     * Drops elements while the predicate is true and returns the rest.
     *
     * <p>This is an intermediate operation.
     *
     * <p>Example:
     * <pre>
     * predicate: (a) -&gt; a &lt; 3
     * stream: [1, 2, 3, 4, 1, 2, 3, 4]
     * result: [3, 4, 1, 2, 3, 4]
     * </pre>
     *
     * @param predicate  the predicate used to drop elements
     * @return the new {@code LongStream}
     */
    public LongStream dropWhile(final LongPredicate predicate) {
        return new LongStream(params, new LongDropWhile(iterator, predicate));
    }

    /**
     * Returns a stream consisting of the elements of this stream, truncated
     * to be no longer than {@code maxSize} in length.
     *
     * <p> This is a short-circuiting stateful intermediate operation.
     *
     * <p>Example:
     * <pre>
     * maxSize: 3
     * stream: [1, 2, 3, 4, 5]
     * result: [1, 2, 3]
     *
     * maxSize: 10
     * stream: [1, 2]
     * result: [1, 2]
     * </pre>
     *
     * @param maxSize  the number of elements the stream should be limited to
     * @return the new stream
     * @throws IllegalArgumentException if {@code maxSize} is negative
     */
    public LongStream limit(final long maxSize) {
        if (maxSize < 0)
            throw new IllegalArgumentException("maxSize cannot be negative");
        if (maxSize == 0)
            return LongStream.empty();
        return new LongStream(params, new LongLimit(iterator, maxSize));
    }

    /**
     * Skips first {@code n} elements and returns {@code LongStream} with remaining elements.
     * If this stream contains fewer than {@code n} elements, then an
     * empty stream will be returned.
     *
     * <p>This is a stateful intermediate operation.
     *
     * <p>Example:
     * <pre>
     * n: 3
     * stream: [1, 2, 3, 4, 5]
     * result: [4, 5]
     *
     * n: 10
     * stream: [1, 2]
     * result: []
     * </pre>
     *
     * @param n  the number of elements to skip
     * @return the new stream
     * @throws IllegalArgumentException if {@code n} is negative
     */
    public LongStream skip(final long n) {
        if (n < 0)
            throw new IllegalArgumentException("n cannot be negative");
        if (n == 0)
            return this;
        return new LongStream(params, new LongSkip(iterator, n));
    }

    /**
     * Performs an action for each element of this stream.
     *
     * <p>This is a terminal operation.
     *
     * @param action  the action to be performed on each element
     */
    public void forEach(LongConsumer action) {
        while (iterator.hasNext()) {
            action.accept(iterator.nextLong());
        }
    }

    /**
     * Performs a reduction on the elements of this stream, using the provided
     * identity value and an associative accumulation function, and returns the
     * reduced value.
     *
     * <p>The {@code identity} value must be an identity for the accumulator
     * function. This means that for all {@code x},
     * {@code accumulator.apply(identity, x)} is equal to {@code x}.
     * The {@code accumulator} function must be an associative function.
     *
     * <p>This is a terminal operation.
     *
     * <p>Example:
     * <pre>
     * identity: 0
     * accumulator: (a, b) -&gt; a + b
     * stream: [1, 2, 3, 4, 5]
     * result: 15
     * </pre>
     *
     * @param identity  the identity value for the accumulating function
     * @param accumulator  the accumulation function
     * @return the result of the reduction
     * @see #sum()
     * @see #min()
     * @see #max()
     */
    public long reduce(long identity, LongBinaryOperator accumulator) {
        long result = identity;
        while (iterator.hasNext()) {
            final long value = iterator.nextLong();
            result = accumulator.applyAsLong(result, value);
        }
        return result;
    }

    /**
     * Performs a reduction on the elements of this stream, using an
     * associative accumulation function, and returns an {@code OptionalLong}
     * describing the reduced value, if any.
     *
     * <p>The {@code accumulator} function must be an associative function.
     *
     * <p>This is a terminal operation.
     *
     * @param accumulator  the accumulation function
     * @return the result of the reduction
     * @see #reduce(com.landawn.abacus.util.function.LongBinaryOperator)
     */
    public OptionalLong reduce(LongBinaryOperator accumulator) {
        boolean foundAny = false;
        long result = 0;
        while (iterator.hasNext()) {
            final long value = iterator.nextLong();
            if (!foundAny) {
                foundAny = true;
                result = value;
            } else {
                result = accumulator.applyAsLong(result, value);
            }
        }
        return foundAny ? OptionalLong.of(result) : OptionalLong.empty();
    }

    /**
     * Returns an array containing the elements of this stream.
     *
     * <p>This is a terminal operation.
     *
     * @return an array containing the elements of this stream
     */
    public long[] toArray() {
        return Operators.toLongArray(iterator);
    }

    /**
     * Collects elements to {@code supplier} provided container by applying the given accumulation function.
     *
     * <p>This is a terminal operation.
     *
     * @param <R> the type of the result
     * @param supplier  the supplier function that provides container
     * @param accumulator  the accumulation function
     * @return the result of collect elements
     * @see Stream#collect(com.landawn.abacus.util.function.Supplier, com.landawn.abacus.util.function.BiConsumer)
     */
    public <R> R collect(Supplier<R> supplier, ObjLongConsumer<R> accumulator) {
        final R result = supplier.get();
        while (iterator.hasNext()) {
            final long value = iterator.nextLong();
            accumulator.accept(result, value);
        }
        return result;
    }

    /**
     * Returns the sum of elements in this stream.
     *
     * @return the sum of elements in this stream
     */
    public long sum() {
        long sum = 0;
        while (iterator.hasNext()) {
            sum += iterator.nextLong();
        }
        return sum;
    }

    /**
     * Returns an {@code OptionalLong} describing the minimum element of this
     * stream, or an empty optional if this stream is empty.
     *
     * <p>This is a terminal operation.
     *
     * @return the minimum element
     */
    public OptionalLong min() {
        return reduce(new LongBinaryOperator() {
            @Override
            public long applyAsLong(long left, long right) {
                return Math.min(left, right);
            }
        });
    }

    /**
     * Returns an {@code OptionalLong} describing the maximum element of this
     * stream, or an empty optional if this stream is empty.
     *
     * <p>This is a terminal operation.
     *
     * @return the maximum element
     */
    public OptionalLong max() {
        return reduce(new LongBinaryOperator() {
            @Override
            public long applyAsLong(long left, long right) {
                return Math.max(left, right);
            }
        });
    }

    /**
     * Returns the count of elements in this stream.
     *
     * <p>This is a terminal operation.
     *
     * @return the count of elements in this stream
     */
    public int count() {
        int count = 0;
        while (iterator.hasNext()) {
            iterator.nextLong();
            count++;
        }
        return count;
    }

    /**
    * Returns the average of elements in this stream.
    *
    * <p>This is a terminal operation.
    *
    * @return the average of elements in this stream
    */
    public OptionalDouble average() {
        long count = 0;
        long sum = 0;
        while (iterator.hasNext()) {
            sum += iterator.nextLong();
            count++;
        }
        if (count == 0)
            return OptionalDouble.empty();
        return OptionalDouble.of(((double) sum) / count);
    }

    /**
     * Tests whether all elements match the given predicate.
     * May not evaluate the predicate on all elements if not necessary
     * for determining the result. If the stream is empty then
     * {@code false} is returned and the predicate is not evaluated.
     *
     * <p>This is a short-circuiting terminal operation.
     *
     * <p>Example:
     * <pre>
     * predicate: (a) -&gt; a == 5
     * stream: [1, 2, 3, 4, 5]
     * result: true
     *
     * predicate: (a) -&gt; a == 5
     * stream: [5, 5, 5]
     * result: true
     * </pre>
     *
     * @param predicate  the predicate used to match elements
     * @return {@code true} if any elements of the stream match the provided
     *         predicate, otherwise {@code false}
     */
    public boolean anyMatch(LongPredicate predicate) {
        while (iterator.hasNext()) {
            if (predicate.test(iterator.nextLong()))
                return true;
        }
        return false;
    }

    /**
     * Tests whether all elements match the given predicate.
     * May not evaluate the predicate on all elements if not necessary for
     * determining the result. If the stream is empty then {@code true} is
     * returned and the predicate is not evaluated.
     *
     * <p>This is a short-circuiting terminal operation.
     *
     * <p>Example:
     * <pre>
     * predicate: (a) -&gt; a == 5
     * stream: [1, 2, 3, 4, 5]
     * result: false
     *
     * predicate: (a) -&gt; a == 5
     * stream: [5, 5, 5]
     * result: true
     * </pre>
     *
     * @param predicate  the predicate used to match elements
     * @return {@code true} if either all elements of the stream match the
     *         provided predicate or the stream is empty, otherwise {@code false}
     */
    public boolean allMatch(LongPredicate predicate) {
        while (iterator.hasNext()) {
            if (!predicate.test(iterator.nextLong()))
                return false;
        }
        return true;
    }

    /**
     * Tests whether no elements match the given predicate.
     * May not evaluate the predicate on all elements if not necessary for
     * determining the result. If the stream is empty then {@code true} is
     * returned and the predicate is not evaluated.
     *
     * <p>This is a short-circuiting terminal operation.
     *
     * <p>Example:
     * <pre>
     * predicate: (a) -&gt; a == 5
     * stream: [1, 2, 3, 4, 5]
     * result: false
     *
     * predicate: (a) -&gt; a == 5
     * stream: [1, 2, 3]
     * result: true
     * </pre>
     *
     * @param predicate  the predicate used to match elements
     * @return {@code true} if either no elements of the stream match the
     *         provided predicate or the stream is empty, otherwise {@code false}
     */
    public boolean noneMatch(LongPredicate predicate) {
        while (iterator.hasNext()) {
            if (predicate.test(iterator.nextLong()))
                return false;
        }
        return true;
    }

    /**
     * Returns the first element wrapped by {@code OptionalLong} class.
     * If stream is empty, returns {@code OptionalLong.empty()}.
     *
     * <p>This is a short-circuiting terminal operation.
     *
     * @return an {@code OptionalLong} with first element
     *         or {@code OptionalLong.empty()} if stream is empty
     */
    public OptionalLong findFirst() {
        if (iterator.hasNext()) {
            return OptionalLong.of(iterator.nextLong());
        }
        return OptionalLong.empty();
    }

    /**
     * Returns the last element wrapped by {@code OptionalLong} class.
     * If stream is empty, returns {@code OptionalLong.empty()}.
     *
     * <p>This is a short-circuiting terminal operation.
     *
     * @return an {@code OptionalLong} with the last element
     *         or {@code OptionalLong.empty()} if the stream is empty
     * @since 1.1.8
     */
    public OptionalLong findLast() {
        return reduce(new LongBinaryOperator() {
            @Override
            public long applyAsLong(long left, long right) {
                return right;
            }
        });
    }

    /**
     * Applies custom operator on stream.
     *
     * Transforming function can return {@code LongStream} for intermediate operations,
     * or any value for terminal operation.
     *
     * <p>Operator examples:
     * <pre><code>
     *     // Intermediate operator
     *     public class Zip implements Function&lt;LongStream, LongStream&gt; {
     *
     *         private final LongStream secondStream;
     *         private final LongBinaryOperator combiner;
     *
     *         public Zip(LongStream secondStream, LongBinaryOperator combiner) {
     *             this.secondStream = secondStream;
     *             this.combiner = combiner;
     *         }
     *
     *         &#64;Override
     *         public LongStream apply(LongStream firstStream) {
     *             final PrimitiveIterator.OfLong it1 = firstStream.iterator();
     *             final PrimitiveIterator.OfLong it2 = secondStream.iterator();
     *             return LongStream.of(new PrimitiveIterator.OfLong() {
     *                 &#64;Override
     *                 public boolean hasNext() {
     *                     return it1.hasNext() &amp;&amp; it2.hasNext();
     *                 }
     *
     *                 &#64;Override
     *                 public long nextLong() {
     *                     return combiner.applyAsLong(it1.nextLong(), it2.nextLong());
     *                 }
     *             });
     *         }
     *     }
     *
     *     // Intermediate operator based on existing stream operators
     *     public class SkipAndLimit implements UnaryOperator&lt;LongStream&gt; {
     *
     *         private final int skip, limit;
     *
     *         public SkipAndLimit(int skip, int limit) {
     *             this.skip = skip;
     *             this.limit = limit;
     *         }
     *
     *         &#64;Override
     *         public LongStream apply(LongStream stream) {
     *             return stream.skip(skip).limit(limit);
     *         }
     *     }
     *
     *     // Terminal operator
     *     public class LongSummaryStatistics implements Function&lt;LongStream, long[]&gt; {
     *         &#64;Override
     *         public long[] apply(LongStream stream) {
     *             long count = 0;
     *             long sum = 0;
     *             final PrimitiveIterator.OfLong it = stream.iterator();
     *             while (it.hasNext()) {
     *                 count++;
     *                 sum += it.nextLong();
     *             }
     *             return new long[] {count, sum};
     *         }
     *     }
     * </code></pre>
     *
     * @param <R> the type of the result
     * @param function  a transforming function
     * @return a result of the transforming function
     * @see Stream#chain(com.landawn.abacus.util.function.Function)
     * @throws NullPointerException if {@code function} is null
     */
    public <R> R __(Function<? super LongStream, R> transfer) {
        return transfer.apply(this);
    }

    public void println() {
        boxed().println();
    }

    /**
     * Adds close handler to the current stream.
     *
     * <p>This is an intermediate operation.
     *
     * @param closeHandler  an action to execute when the stream is closed
     * @return the new stream with the close handler
     * @since 1.1.8
     */
    public LongStream onClose(final Runnable closeHandler) {
        N.requireNonNull(closeHandler);
        final Params newParams;
        if (params == null) {
            newParams = new Params();
            newParams.closeHandler = closeHandler;
        } else {
            newParams = params;
            final Runnable firstHandler = newParams.closeHandler;
            newParams.closeHandler = Compose.runnables(firstHandler, closeHandler);
        }
        return new LongStream(newParams, iterator);
    }

    /**
     * Causes close handler to be invoked if it exists.
     * Since most of the stream providers are lists or arrays,
     * it is not necessary to close the stream.
     *
     * @since 1.1.8
     */
    @Override
    public void close() {
        if (params != null && params.closeHandler != null) {
            params.closeHandler.run();
            params.closeHandler = null;
        }
    }

    private static final ToLongFunction<Long> UNBOX_FUNCTION = new ToLongFunction<Long>() {
        @Override
        public long applyAsLong(Long t) {
            return t;
        }
    };
}
