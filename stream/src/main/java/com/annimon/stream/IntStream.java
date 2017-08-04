package com.annimon.stream;

import java.io.Closeable;
import java.util.Comparator;
import java.util.NoSuchElementException;

import com.annimon.stream.internal.Compose;
import com.annimon.stream.internal.Operators;
import com.annimon.stream.internal.Params;
import com.annimon.stream.iterator.PrimitiveIterator;
import com.annimon.stream.operator.IntArray;
import com.annimon.stream.operator.IntCodePoints;
import com.annimon.stream.operator.IntConcat;
import com.annimon.stream.operator.IntDropWhile;
import com.annimon.stream.operator.IntFilter;
import com.annimon.stream.operator.IntFlatMap;
import com.annimon.stream.operator.IntGenerate;
import com.annimon.stream.operator.IntIterate;
import com.annimon.stream.operator.IntLimit;
import com.annimon.stream.operator.IntMap;
import com.annimon.stream.operator.IntMapToDouble;
import com.annimon.stream.operator.IntMapToLong;
import com.annimon.stream.operator.IntMapToObj;
import com.annimon.stream.operator.IntPeek;
import com.annimon.stream.operator.IntRangeClosed;
import com.annimon.stream.operator.IntScan;
import com.annimon.stream.operator.IntScanIdentity;
import com.annimon.stream.operator.IntSkip;
import com.annimon.stream.operator.IntSorted;
import com.annimon.stream.operator.IntTakeUntil;
import com.annimon.stream.operator.IntTakeWhile;
import com.landawn.abacus.util.OptionalDouble;
import com.landawn.abacus.util.OptionalInt;
import com.landawn.abacus.util.function.Function;
import com.landawn.abacus.util.function.IntBinaryOperator;
import com.landawn.abacus.util.function.IntConsumer;
import com.landawn.abacus.util.function.IntFunction;
import com.landawn.abacus.util.function.IntPredicate;
import com.landawn.abacus.util.function.IntSupplier;
import com.landawn.abacus.util.function.IntToDoubleFunction;
import com.landawn.abacus.util.function.IntToLongFunction;
import com.landawn.abacus.util.function.IntUnaryOperator;
import com.landawn.abacus.util.function.ObjIntConsumer;
import com.landawn.abacus.util.function.Supplier;
import com.landawn.abacus.util.function.ToIntFunction;

/**
 * A sequence of primitive int-valued elements supporting sequential operations. This is the {@code int}
 * primitive specialization of {@link Stream}.
 */
public final class IntStream implements Closeable {

    /**
     * Single instance for empty stream. It is safe for multi-thread environment because it has no content.
     */
    private static final IntStream EMPTY = of(new int[0]);

    /**
     * Returns an empty stream.
     *
     * @return the empty stream
     */
    public static IntStream empty() {
        return EMPTY;
    }

    /**
     * Returns stream whose elements are the specified values.
     *
     * @param values the elements of the new stream
     * @return the new stream
     * @throws NullPointerException if {@code values} is null
     */
    public static IntStream of(final int... values) {
        Objects.requireNonNull(values);
        if (values.length == 0) {
            return IntStream.empty();
        }
        return new IntStream(new IntArray(values));
    }

    /**
     * Creates a {@code IntStream} from {@code PrimitiveIterator.OfInt}.
     *
     * @param iterator  the iterator with elements to be passed to stream
     * @return the new {@code IntStream}
     * @throws NullPointerException if {@code iterator} is null
     */
    public static IntStream of(PrimitiveIterator.OfInt iterator) {
        Objects.requireNonNull(iterator);
        return new IntStream(iterator);
    }

    /**
     * Creates an {@code IntStream} of code point values from the given sequence.
     * Any surrogate pairs encountered in the sequence are combined as if by {@linkplain
     * Character#toCodePoint Character.toCodePoint} and the result is passed to the stream.
     * Any other code units, including ordinary BMP characters, unpaired surrogates, and
     * undefined code units, are zero-extended to {@code int} values which are then
     * passed to the stream.
     *
     * @param charSequence  the sequence where to get all code points values.
     * @return the new stream
     * @since 1.1.8
     */
    public static IntStream ofCodePoints(CharSequence charSequence) {
        return new IntStream(new IntCodePoints(charSequence));
    }

    /**
     * Returns a sequential ordered {@code IntStream} from {@code startInclusive}
     * (inclusive) to {@code endExclusive} (exclusive) by an incremental step of
     * {@code 1}.
     *
     * @param startInclusive the (inclusive) initial value
     * @param endExclusive the exclusive upper bound
     * @return a sequential {@code IntStream} for the range of {@code int}
     *         elements
     */
    public static IntStream range(final int startInclusive, final int endExclusive) {
        if (startInclusive >= endExclusive) {
            return empty();
        }
        return rangeClosed(startInclusive, endExclusive - 1);
    }

    public static IntStream range(final int startInclusive, final int endExclusive, final int by) {
        if (by == 0) {
            throw new IllegalArgumentException("'by' can't be zero");
        }

        if (endExclusive == startInclusive || endExclusive > startInclusive != by > 0) {
            return empty();
        }

        return of(new PrimitiveIterator.OfInt() {
            private int next = startInclusive;
            private long cnt = (endExclusive * 1L - startInclusive) / by + ((endExclusive * 1L - startInclusive) % by == 0 ? 0 : 1);

            @Override
            public boolean hasNext() {
                return cnt > 0;
            }

            @Override
            public int nextInt() {
                if (cnt-- <= 0) {
                    throw new NoSuchElementException();
                }

                int result = next;
                next += by;
                return result;
            }
        });
    }

    /**
     * Returns a sequential ordered {@code IntStream} from {@code startInclusive}
     * (inclusive) to {@code endInclusive} (inclusive) by an incremental step of
     * {@code 1}.
     *
     * @param startInclusive the (inclusive) initial value
     * @param endInclusive the inclusive upper bound
     * @return a sequential {@code IntStream} for the range of {@code int}
     *         elements
     */
    public static IntStream rangeClosed(final int startInclusive, final int endInclusive) {
        if (startInclusive > endInclusive) {
            return empty();
        } else if (startInclusive == endInclusive) {
            return of(startInclusive);
        } else {
            return new IntStream(new IntRangeClosed(startInclusive, endInclusive));
        }
    }

    public static IntStream rangeClosed(final int startInclusive, final int endInclusive, final int by) {
        if (by == 0) {
            throw new IllegalArgumentException("'by' can't be zero");
        }

        if (endInclusive == startInclusive) {
            return of(startInclusive);
        } else if (endInclusive > startInclusive != by > 0) {
            return empty();
        }

        return of(new PrimitiveIterator.OfInt() {
            private int next = startInclusive;
            private long cnt = (endInclusive * 1L - startInclusive) / by + 1;

            @Override
            public boolean hasNext() {
                return cnt > 0;
            }

            @Override
            public int nextInt() {
                if (cnt-- <= 0) {
                    throw new NoSuchElementException();
                }

                int result = next;
                next += by;
                return result;
            }
        });
    }

    /**
     * Returns an infinite sequential unordered stream where each element is
     * generated by the provided {@code IntSupplier}.  This is suitable for
     * generating constant streams, streams of random elements, etc.
     *
     * @param s the {@code IntSupplier} for generated elements
     * @return a new infinite sequential {@code IntStream}
     * @throws NullPointerException if {@code s} is null
     */
    public static IntStream generate(final IntSupplier s) {
        Objects.requireNonNull(s);
        return new IntStream(new IntGenerate(s));
    }

    /**
     * Returns an infinite sequential ordered {@code IntStream} produced by iterative
     * application of a function {@code f} to an initial element {@code seed},
     * producing a {@code Stream} consisting of {@code seed}, {@code f(seed)},
     * {@code f(f(seed))}, etc.
     *
     * <p> The first element (position {@code 0}) in the {@code IntStream} will be
     * the provided {@code seed}.  For {@code n > 0}, the element at position
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
     * @param f a function to be applied to the previous element to produce
     *          a new element
     * @return a new sequential {@code IntStream}
     * @throws NullPointerException if {@code f} is null
     */
    public static IntStream iterate(final int seed, final IntUnaryOperator f) {
        Objects.requireNonNull(f);
        return new IntStream(new IntIterate(seed, f));
    }

    /**
     * Creates an {@code IntStream} by iterative application {@code IntUnaryOperator} function
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
    public static IntStream iterate(final int seed, final IntPredicate predicate, final IntUnaryOperator op) {
        Objects.requireNonNull(predicate);
        return iterate(seed, op).takeWhile(predicate);
    }

    /**
     * Creates a lazily concatenated stream whose elements are all the
     * elements of the first stream followed by all the elements of the
     * second stream.
     *
     * <p>Example:
     * <pre>
     * stream a: [1, 2, 3, 4]
     * stream b: [5, 6]
     * result:   [1, 2, 3, 4, 5, 6]
     * </pre>
     *
     * @param a the first stream
     * @param b the second stream
     * @return the concatenation of the two input streams
     * @throws NullPointerException if {@code a} or {@code b} is null
     */
    public static IntStream concat(final IntStream a, final IntStream b) {
        Objects.requireNonNull(a);
        Objects.requireNonNull(b);
        @SuppressWarnings("resource")
        IntStream result = new IntStream(new IntConcat(a.iterator, b.iterator));
        return result.onClose(Compose.closeables(a, b));
    }

    public static IntStream concat(final int[] a, final int[] b) {
        return concat(IntStream.of(a), IntStream.of(b));
    }

    private final PrimitiveIterator.OfInt iterator;
    private final Params params;

    private IntStream(PrimitiveIterator.OfInt iterator) {
        this(null, iterator);
    }

    IntStream(Params params, PrimitiveIterator.OfInt iterator) {
        this.params = params;
        this.iterator = iterator;
    }

    /**
     * Returns internal {@code IntStream} iterator.
     *
     * @return internal {@code IntStream} iterator.
     */
    public PrimitiveIterator.OfInt iterator() {
        return iterator;
    }

    /**
     * Returns a {@code Stream} consisting of the elements of this stream,
     * each boxed to an {@code Integer}.
     *
     * <p>This is an lazy intermediate operation.
     *
     * @return a {@code Stream} consistent of the elements of this stream,
     *         each boxed to an {@code Integer}
     */
    public Stream<Integer> boxed() {
        return new Stream<>(params, iterator);
    }

    /**
     * Returns a stream consisting of the elements of this stream that match
     * the given predicate.
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
     * @param predicate non-interfering, stateless predicate to apply to each
     *                  element to determine if it should be included
     * @return the new stream
     */
    public IntStream filter(final IntPredicate predicate) {
        return new IntStream(params, new IntFilter(iterator, predicate));
    }

    /**
     * Returns an {@code IntStream} consisting of the results of applying the given
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
     * @param mapper a non-interfering stateless function to apply to
     *               each element
     * @return the new {@code IntStream}
     */
    public IntStream map(final IntUnaryOperator mapper) {
        return new IntStream(params, new IntMap(iterator, mapper));
    }

    /**
     * Returns a {@code LongStream} consisting of the results of applying the given
     * function to the elements of this stream.
     *
     * <p> This is an intermediate operation.
     *
     * @param mapper  the mapper function used to apply to each element
     * @return the new {@code LongStream}
     * @since 1.1.4
     * @see #flatMap(com.landawn.abacus.util.function.IntFunction)
     */
    public LongStream mapToLong(final IntToLongFunction mapper) {
        return new LongStream(params, new IntMapToLong(iterator, mapper));
    }

    /**
     * Returns a {@code DoubleStream} consisting of the results of applying the given
     * function to the elements of this stream.
     *
     * <p> This is an intermediate operation.
     *
     * @param mapper  the mapper function used to apply to each element
     * @return the new {@code DoubleStream}
     * @since 1.1.4
     * @see #flatMap(com.landawn.abacus.util.function.IntFunction)
     */
    public DoubleStream mapToDouble(final IntToDoubleFunction mapper) {
        return new DoubleStream(params, new IntMapToDouble(iterator, mapper));
    }

    /**
     * Returns a {@code Stream} consisting of the results of applying the given
     * function to the elements of this stream.
     *
     * <p> This is an intermediate operation.
     *
     * @param <R> the type result
     * @param mapper the mapper function used to apply to each element
     * @return the new {@code Stream}
     */
    public <R> Stream<R> mapToObj(final IntFunction<? extends R> mapper) {
        return new Stream<>(params, new IntMapToObj<>(iterator, mapper));
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
     * @param mapper a non-interfering stateless function to apply to each
     *               element which produces an {@code IntStream} of new values
     * @return the new stream
     * @see Stream#flatMap(Function)
     */
    public IntStream flatMap(final IntFunction<? extends IntStream> mapper) {
        return new IntStream(params, new IntFlatMap(iterator, mapper));
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
    public IntStream distinct() {
        // While functional and quick to implement, this approach is not very efficient.
        // An efficient version requires an int-specific map/set implementation.
        return boxed().distinct().mapToInt(UNBOX_FUNCTION);
    }

    /**
     * Returns a stream consisting of the elements of this stream in sorted
     * order.
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
    public IntStream sorted() {
        return new IntStream(params, new IntSorted(iterator));
    }

    /**
     * Returns {@code IntStream} with sorted elements (as determinated by provided {@code Comparator}).
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
     * @return the new {@code IntStream}
     */
    public IntStream sorted(Comparator<Integer> comparator) {
        return boxed().sorted(comparator).mapToInt(UNBOX_FUNCTION);
    }

    /**
     * Returns a stream consisting of the elements of this stream, additionally
     * performing the provided action on each element as elements are consumed
     * from the resulting stream. Handy method for debugging purposes.
     *
     * <p>This is an intermediate operation.
     *
     * @param action the action to be performed on each element
     * @return the new stream
     */
    public IntStream peek(final IntConsumer action) {
        return new IntStream(params, new IntPeek(iterator, action));
    }

    /**
     * Returns a {@code IntStream} produced by iterative application of a accumulation function
     * to reduction value and next element of the current stream.
     * Produces a {@code IntStream} consisting of {@code value1}, {@code acc(value1, value2)},
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
    public IntStream scan(final IntBinaryOperator accumulator) {
        Objects.requireNonNull(accumulator);
        return new IntStream(params, new IntScan(iterator, accumulator));
    }

    /**
     * Returns a {@code IntStream} produced by iterative application of a accumulation function
     * to an initial element {@code identity} and next element of the current stream.
     * Produces a {@code IntStream} consisting of {@code identity}, {@code acc(identity, value1)},
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
    public IntStream scan(final int identity, final IntBinaryOperator accumulator) {
        Objects.requireNonNull(accumulator);
        return new IntStream(params, new IntScanIdentity(iterator, identity, accumulator));
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
     * @return the new {@code IntStream}
     */
    public IntStream takeWhile(final IntPredicate predicate) {
        return new IntStream(params, new IntTakeWhile(iterator, predicate));
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
     * @return the new {@code IntStream}
     * @since 1.1.6
     */
    public IntStream takeUntil(final IntPredicate stopPredicate) {
        return new IntStream(params, new IntTakeUntil(iterator, stopPredicate));
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
     * @return the new {@code IntStream}
     */
    public IntStream dropWhile(final IntPredicate predicate) {
        return new IntStream(params, new IntDropWhile(iterator, predicate));
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
     * @param maxSize the number of elements the stream should be limited to
     * @return the new stream
     * @throws IllegalArgumentException if {@code maxSize} is negative
     */
    public IntStream limit(final long maxSize) {
        if (maxSize < 0) {
            throw new IllegalArgumentException("maxSize cannot be negative");
        }
        if (maxSize == 0) {
            return IntStream.empty();
        }
        return new IntStream(params, new IntLimit(iterator, maxSize));
    }

    /**
     * Returns a stream consisting of the remaining elements of this stream
     * after discarding the first {@code n} elements of the stream.
     * If this stream contains fewer than {@code n} elements then an
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
     * @param n the number of leading elements to skip
     * @return the new stream
     * @throws IllegalArgumentException if {@code n} is negative
     */
    public IntStream skip(final long n) {
        if (n < 0) {
            throw new IllegalArgumentException("n cannot be negative");
        } else if (n == 0) {
            return this;
        } else {
            return new IntStream(params, new IntSkip(iterator, n));
        }
    }

    /**
     * Returns the sum of elements in this stream.
     *
     * @return the sum of elements in this stream
     */
    public int sum() {
        int sum = 0;
        while (iterator.hasNext()) {
            sum += iterator.nextInt();
        }

        return sum;
    }

    /**
     * Returns an {@code OptionalInt} describing the minimum element of this
     * stream, or an empty optional if this stream is empty.
     *
     * <p>This is a terminal operation.
     *
     * @return an {@code OptionalInt} containing the minimum element of this
     *         stream, or an empty {@code OptionalInt} if the stream is empty
     */
    public OptionalInt min() {
        return reduce(new IntBinaryOperator() {
            @Override
            public int applyAsInt(int left, int right) {
                return left < right ? left : right;
            }
        });
    }

    /**
     * Returns an {@code OptionalInt} describing the maximum element of this
     * stream, or an empty optional if this stream is empty.
     *
     * <p>This is a terminal operation.
     *
     * @return an {@code OptionalInt} containing the maximum element of this
     *         stream, or an empty {@code OptionalInt} if the stream is empty
     */
    public OptionalInt max() {
        return reduce(new IntBinaryOperator() {
            @Override
            public int applyAsInt(int left, int right) {
                return left > right ? left : right;
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
            iterator.nextInt();
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
            sum += iterator.nextInt();
            count++;
        }
        if (count == 0)
            return OptionalDouble.empty();
        return OptionalDouble.of(((double) sum) / count);
    }

    /**
     * Returns whether any elements of this stream match the provided
     * predicate. May not evaluate the predicate on all elements if not
     * necessary for determining the result.  If the stream is empty then
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
     * @param predicate a non-interfering stateless predicate to apply
     *                  to elements of this stream
     * @return {@code true} if any elements of the stream match the provided
     *         predicate, otherwise {@code false}
     */
    public boolean anyMatch(IntPredicate predicate) {
        while (iterator.hasNext()) {
            if (predicate.test(iterator.nextInt()))
                return true;
        }

        return false;
    }

    /**
     * Returns whether all elements of this stream match the provided predicate.
     * May not evaluate the predicate on all elements if not necessary for
     * determining the result.  If the stream is empty then {@code true} is
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
     * @param predicate a non-interfering stateless predicate to apply to
     *                  elements of this stream
     * @return {@code true} if either all elements of the stream match the
     *         provided predicate or the stream is empty, otherwise {@code false}
     */
    public boolean allMatch(IntPredicate predicate) {
        while (iterator.hasNext()) {
            if (!predicate.test(iterator.nextInt()))
                return false;
        }

        return true;
    }

    /**
     * Returns whether no elements of this stream match the provided predicate.
     * May not evaluate the predicate on all elements if not necessary for
     * determining the result.  If the stream is empty then {@code true} is
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
     * @param predicate a non-interfering stateless predicate to apply to
     *                  elements of this stream
     * @return {@code true} if either no elements of the stream match the
     *         provided predicate or the stream is empty, otherwise {@code false}
     */
    public boolean noneMatch(IntPredicate predicate) {
        while (iterator.hasNext()) {
            if (predicate.test(iterator.nextInt()))
                return false;
        }
        return true;
    }

    /**
     * Returns an {@link OptionalInt} describing the first element of this
     * stream, or an empty {@code OptionalInt} if the stream is empty.
     *
     * <p>This is a short-circuiting terminal operation.
     *
     * @return an {@code OptionalInt} describing the first element of this stream,
     *         or an empty {@code OptionalInt} if the stream is empty
     */
    public OptionalInt findFirst() {
        if (iterator.hasNext()) {
            return OptionalInt.of(iterator.nextInt());
        } else {
            return OptionalInt.empty();
        }
    }

    /**
     * Returns the last element wrapped by {@code OptionalInt} class.
     * If stream is empty, returns {@code OptionalInt.empty()}.
     *
     * <p>This is a short-circuiting terminal operation.
     *
     * @return an {@code OptionalInt} with the last element
     *         or {@code OptionalInt.empty()} if the stream is empty
     * @since 1.1.8
     */
    public OptionalInt findLast() {
        return reduce(new IntBinaryOperator() {
            @Override
            public int applyAsInt(int left, int right) {
                return right;
            }
        });
    }

    /**
     * Performs an action for each element of this stream.
     *
     * <p>This is a terminal operation.
     *
     * @param action a non-interfering action to perform on the elements
     */
    public void forEach(IntConsumer action) {
        while (iterator.hasNext()) {
            action.accept(iterator.nextInt());
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
     * @param identity the identity value for the accumulating function
     * @param op an associative non-interfering stateless function for
     *           combining two values
     * @return the result of the reduction
     * @see #sum()
     * @see #min()
     * @see #max()
     */
    public int reduce(int identity, IntBinaryOperator op) {
        int result = identity;
        while (iterator.hasNext()) {
            int value = iterator.nextInt();
            result = op.applyAsInt(result, value);
        }
        return result;
    }

    /**
     * Performs a reduction on the elements of this stream, using an
     * associative accumulation function, and returns an {@code OptionalInt}
     * describing the reduced value, if any.
     *
     * <p>The {@code op} function must be an associative function.
     *
     * <p>This is a terminal operation.
     *
     * @param op an associative, non-interfering, stateless function for
     *           combining two values
     * @return the result of the reduction
     * @see #reduce(int, IntBinaryOperator)
     */
    public OptionalInt reduce(IntBinaryOperator op) {
        boolean foundAny = false;
        int result = 0;
        while (iterator.hasNext()) {
            int value = iterator.nextInt();

            if (!foundAny) {
                foundAny = true;
                result = value;
            } else {
                result = op.applyAsInt(result, value);
            }
        }
        return foundAny ? OptionalInt.of(result) : OptionalInt.empty();
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
    public <R> R collect(Supplier<R> supplier, ObjIntConsumer<R> accumulator) {
        R result = supplier.get();
        while (iterator.hasNext()) {
            final int value = iterator.nextInt();
            accumulator.accept(result, value);
        }
        return result;
    }

    /**
     * Returns an array containing the elements of this stream.
     *
     * <p>This is a terminal operation.
     *
     * @return an array containing the elements of this stream
     */
    public int[] toArray() {
        return Operators.toIntArray(iterator);
    }

    /**
     * Applies custom operator on stream.
     *
     * Transforming function can return {@code IntStream} for intermediate operations,
     * or any value for terminal operation.
     *
     * <p>Operator examples:
     * <pre><code>
     *     // Intermediate operator
     *     public class Zip&lt;T&gt; implements Function&lt;IntStream, IntStream&gt; {
     *         &#64;Override
     *         public IntStream apply(IntStream firstStream) {
     *             final PrimitiveIterator.OfInt it1 = firstStream.iterator();
     *             final PrimitiveIterator.OfInt it2 = secondStream.iterator();
     *             return IntStream.of(new PrimitiveIterator.OfInt() {
     *                 &#64;Override
     *                 public boolean hasNext() {
     *                     return it1.hasNext() &amp;&amp; it2.hasNext();
     *                 }
     *
     *                 &#64;Override
     *                 public int nextInt() {
     *                     return combiner.applyAsInt(it1.nextInt(), it2.nextInt());
     *                 }
     *             });
     *         }
     *     }
     *
     *     // Intermediate operator based on existing stream operators
     *     public class SkipAndLimit implements UnaryOperator&lt;IntStream&gt; {
     *
     *         private final int skip, limit;
     *
     *         public SkipAndLimit(int skip, int limit) {
     *             this.skip = skip;
     *             this.limit = limit;
     *         }
     *
     *         &#64;Override
     *         public IntStream apply(IntStream stream) {
     *             return stream.skip(skip).limit(limit);
     *         }
     *     }
     *
     *     // Terminal operator
     *     public class Average implements Function&lt;IntStream, Double&gt; {
     *         long count = 0, sum = 0;
     *
     *         &#64;Override
     *         public Double apply(IntStream stream) {
     *             final PrimitiveIterator.OfInt it = stream.iterator();
     *             while (it.hasNext()) {
     *                 count++;
     *                 sum += it.nextInt();
     *             }
     *             return (count == 0) ? 0 : sum / (double) count;
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
    public <R> R chain(final Function<IntStream, R> function) {
        Objects.requireNonNull(function);
        return function.apply(this);
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
    public IntStream onClose(final Runnable closeHandler) {
        Objects.requireNonNull(closeHandler);
        final Params newParams;
        if (params == null) {
            newParams = new Params();
            newParams.closeHandler = closeHandler;
        } else {
            newParams = params;
            final Runnable firstHandler = newParams.closeHandler;
            newParams.closeHandler = Compose.runnables(firstHandler, closeHandler);
        }
        return new IntStream(newParams, iterator);
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

    private static final ToIntFunction<Integer> UNBOX_FUNCTION = new ToIntFunction<Integer>() {
        @Override
        public int applyAsInt(Integer t) {
            return t;
        }
    };
}
