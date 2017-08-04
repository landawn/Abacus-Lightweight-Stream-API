package com.annimon.stream;

import java.io.Closeable;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.annimon.stream.function.BiConsumer;
import com.annimon.stream.function.BiFunction;
import com.annimon.stream.function.BinaryOperator;
import com.annimon.stream.function.Consumer;
import com.annimon.stream.function.Function;
import com.annimon.stream.function.IntFunction;
import com.annimon.stream.function.Predicate;
import com.annimon.stream.function.Supplier;
import com.annimon.stream.function.ToDoubleFunction;
import com.annimon.stream.function.ToIntFunction;
import com.annimon.stream.function.ToLongFunction;
import com.annimon.stream.function.UnaryOperator;
import com.annimon.stream.internal.Compose;
import com.annimon.stream.internal.Operators;
import com.annimon.stream.internal.Params;
import com.annimon.stream.iterator.LazyIterator;
import com.annimon.stream.operator.ObjArray;
import com.annimon.stream.operator.ObjChunkBy;
import com.annimon.stream.operator.ObjConcat;
import com.annimon.stream.operator.ObjDistinct;
import com.annimon.stream.operator.ObjDistinctBy;
import com.annimon.stream.operator.ObjDropWhile;
import com.annimon.stream.operator.ObjFilter;
import com.annimon.stream.operator.ObjFlatMap;
import com.annimon.stream.operator.ObjFlatMapToDouble;
import com.annimon.stream.operator.ObjFlatMapToInt;
import com.annimon.stream.operator.ObjFlatMapToLong;
import com.annimon.stream.operator.ObjGenerate;
import com.annimon.stream.operator.ObjIterate;
import com.annimon.stream.operator.ObjLimit;
import com.annimon.stream.operator.ObjMap;
import com.annimon.stream.operator.ObjMapToDouble;
import com.annimon.stream.operator.ObjMapToInt;
import com.annimon.stream.operator.ObjMapToLong;
import com.annimon.stream.operator.ObjMerge;
import com.annimon.stream.operator.ObjPeek;
import com.annimon.stream.operator.ObjScan;
import com.annimon.stream.operator.ObjScanIdentity;
import com.annimon.stream.operator.ObjSkip;
import com.annimon.stream.operator.ObjSlidingWindow;
import com.annimon.stream.operator.ObjSorted;
import com.annimon.stream.operator.ObjTakeUntil;
import com.annimon.stream.operator.ObjTakeWhile;
import com.annimon.stream.operator.ObjZip;

/**
 * A sequence of elements supporting aggregate operations.
 *
 * @param <T> the type of the stream elements
 */
public class Stream<T> implements Closeable {
    static final Object NONE = new Object();

    @SuppressWarnings({ "rawtypes" })
    static final Stream EMPTY = of(new Object[0]);

    /**
     * Returns an empty stream.
     *
     * @param <T> the type of the stream elements
     * @return the new empty stream
     */
    @SuppressWarnings("unchecked")
    public static <T> Stream<T> empty() {
        return EMPTY;
    }

    public static <T> Stream<T> just(final T value) {
        return of(value);
    }

    /**
     * If specified element is null, returns an empty {@code Stream},
     * otherwise returns a {@code Stream} containing a single element.
     *
     * @param <T> the type of the stream element
     * @param element  the element to be passed to stream if it is non-null
     * @return the new stream
     * @since 1.1.5
     */
    public static <T> Stream<T> ofNullable(T element) {
        return (element == null) ? Stream.<T> empty() : Stream.of(element);
    }

    /**
     * Creates a {@code Stream} from the specified values.
     *
     * @param <T> the type of the stream elements
     * @param elements  the elements to be passed to stream
     * @return the new stream
     * @throws NullPointerException if {@code elements} is null
     */
    @SafeVarargs
    public static <T> Stream<T> of(final T... elements) {
        if (elements == null || elements.length == 0) {
            return Stream.<T> empty();
        }

        return new Stream<>(new ObjArray<>(elements));
    }

    /**
     * Creates a {@code Stream} from {@code Map} entries.
     *
     * @param <K> the type of map keys
     * @param <V> the type of map values
     * @param map  the map with elements to be passed to stream
     * @return the new stream
     * @throws NullPointerException if {@code map} is null
     */
    public static <T> Stream<T> of(Collection<? extends T> c) {
        if (c == null || c.size() == 0) {
            return Stream.<T> empty();
        }

        return new Stream<>(c);
    }

    /**
     * Creates a {@code Stream} from {@code Map} entries.
     *
     * @param <K> the type of map keys
     * @param <V> the type of map values
     * @param map  the map with elements to be passed to stream
     * @return the new stream
     * @throws NullPointerException if {@code map} is null
     */
    public static <K, V> Stream<Map.Entry<K, V>> of(Map<K, V> map) {
        if (map == null || map.size() == 0) {
            return Stream.<Map.Entry<K, V>> empty();
        }

        return new Stream<>(map.entrySet());
    }

    /**
     * Creates a {@code Stream} from any class that implements {@code Iterator} interface.
     *
     * @param <T> the type of the stream elements
     * @param iterator  the iterator with elements to be passed to stream
     * @return the new stream
     * @throws NullPointerException if {@code iterator} is null
     */
    public static <T> Stream<T> of(Iterator<? extends T> iterator) {
        Objects.requireNonNull(iterator);
        return new Stream<>(iterator);
    }

    /**
     * Creates a {@code Stream} by elements that generated by {@code Supplier}.
     *
     * @param <T> the type of the stream elements
     * @param supplier  the {@code Supplier} of generated elements
     * @return the new stream
     * @throws NullPointerException if {@code supplier} is null
     */
    public static <T> Stream<T> generate(final Supplier<T> supplier) {
        Objects.requireNonNull(supplier);
        return new Stream<>(new ObjGenerate<>(supplier));
    }

    /**
     * Creates a {@code Stream} by iterative application {@code UnaryOperator} function
     * to an initial element {@code seed}. Produces {@code Stream} consisting of
     * {@code seed}, {@code op(seed)}, {@code op(op(seed))}, etc.
     *
     * <p>Example:
     * <pre>
     * seed: 1
     * op: (a) -&gt; a + 5
     * result: [1, 6, 11, 16, ...]
     * </pre>
     *
     * @param <T> the type of the stream elements
     * @param seed  the initial value
     * @param op  operator to produce new element by previous one
     * @return the new stream
     * @throws NullPointerException if {@code op} is null
     */
    public static <T> Stream<T> iterate(final T seed, final UnaryOperator<T> op) {
        Objects.requireNonNull(op);
        return new Stream<>(new ObjIterate<>(seed, op));
    }

    /**
     * Creates a {@code Stream} by iterative application {@code UnaryOperator} function
     * to an initial element {@code seed}, conditioned on satisfying the supplied predicate.
     *
     * <p>Example:
     * <pre>
     * seed: 0
     * predicate: (a) -&gt; a &lt; 20
     * op: (a) -&gt; a + 5
     * result: [0, 5, 10, 15]
     * </pre>
     *
     * @param <T> the type of the stream elements
     * @param seed  the initial value
     * @param predicate  a predicate to determine when the stream must terminate
     * @param op  operator to produce new element by previous one
     * @return the new stream
     * @throws NullPointerException if {@code op} is null
     * @since 1.1.5
     */
    public static <T> Stream<T> iterate(final T seed, final Predicate<? super T> predicate, final UnaryOperator<T> op) {
        Objects.requireNonNull(predicate);
        return iterate(seed, op).takeWhile(predicate);
    }

    /**
     * Concatenates two streams.
     *
     * <p>Example:
     * <pre>
     * stream 1: [1, 2, 3, 4]
     * stream 2: [5, 6]
     * result:   [1, 2, 3, 4, 5, 6]
     * </pre>
     *
     * @param <T> The type of stream elements
     * @param stream1  the first stream
     * @param stream2  the second stream
     * @return the new concatenated stream
     * @throws NullPointerException if {@code stream1} or {@code stream2} is null
     */
    public static <T> Stream<T> concat(Stream<? extends T> stream1, Stream<? extends T> stream2) {
        Objects.requireNonNull(stream1);
        Objects.requireNonNull(stream2);
        @SuppressWarnings("resource")
        Stream<T> result = new Stream<>(new ObjConcat<>(stream1.iterator, stream2.iterator));
        return result.onClose(Compose.closeables(stream1, stream2));
    }

    /**
     * Concatenates two iterators to a stream.
     *
     * <p>Example:
     * <pre>
     * iterator 1: [1, 2, 3, 4]
     * iterator 2: [5, 6]
     * result:     [1, 2, 3, 4, 5, 6]
     * </pre>
     *
     * @param <T> The type of iterator elements
     * @param iterator1  the first iterator
     * @param iterator2  the second iterator
     * @return the new stream
     * @throws NullPointerException if {@code iterator1} or {@code iterator2} is null
     * @since 1.1.9
     */
    public static <T> Stream<T> concat(Iterator<? extends T> iterator1, Iterator<? extends T> iterator2) {
        Objects.requireNonNull(iterator1);
        Objects.requireNonNull(iterator2);
        return new Stream<>(new ObjConcat<>(iterator1, iterator2));
    }

    /**
     * Combines two streams by applying specified combiner function to each element at same position.
     *
     * <p>Example:
     * <pre>
     * combiner: (a, b) -&gt; a + b
     * stream 1: [1, 2, 3, 4]
     * stream 2: [5, 6, 7, 8]
     * result:   [6, 8, 10, 12]
     * </pre>
     *
     * @param <F> the type of first stream elements
     * @param <S> the type of second stream elements
     * @param <R> the type of elements in resulting stream
     * @param stream1  the first stream
     * @param stream2  the second stream
     * @param combiner  the combiner function used to apply to each element
     * @return the new stream
     * @throws NullPointerException if {@code stream1} or {@code stream2} is null
     */
    public static <F, S, R> Stream<R> zip(Stream<? extends F> stream1, Stream<? extends S> stream2,
            final BiFunction<? super F, ? super S, ? extends R> combiner) {
        Objects.requireNonNull(stream1);
        Objects.requireNonNull(stream2);
        return Stream.<F, S, R> zip(stream1.iterator, stream2.iterator, combiner);
    }

    /**
     * Combines two iterators to a stream by applying specified combiner function to each element at same position.
     *
     * <p>Example:
     * <pre>
     * combiner: (a, b) -&gt; a + b
     * stream 1: [1, 2, 3, 4]
     * stream 2: [5, 6, 7, 8]
     * result:   [6, 8, 10, 12]
     * </pre>
     *
     * @param <F> the type of first iterator elements
     * @param <S> the type of second iterator elements
     * @param <R> the type of elements in resulting stream
     * @param iterator1  the first iterator
     * @param iterator2  the second iterator
     * @param combiner  the combiner function used to apply to each element
     * @return the new stream
     * @throws NullPointerException if {@code iterator1} or {@code iterator2} is null
     * @since 1.1.2
     */
    public static <F, S, R> Stream<R> zip(final Iterator<? extends F> iterator1, final Iterator<? extends S> iterator2,
            final BiFunction<? super F, ? super S, ? extends R> combiner) {
        Objects.requireNonNull(iterator1);
        Objects.requireNonNull(iterator2);
        return new Stream<>(new ObjZip<>(iterator1, iterator2, combiner));
    }

    /**
     * Merges elements of two streams according to the supplied selector function.
     *
     * <p>Example 1 — Merge two sorted streams:
     * <pre>
     * stream1: [1, 3, 8, 10]
     * stream2: [2, 5, 6, 12]
     * selector: (a, b) -&gt; a &lt; b ? TAKE_FIRST : TAKE_SECOND
     * result: [1, 2, 3, 5, 6, 8, 10, 12]
     * </pre>
     *
     * <p>Example 2 — Concat two streams:
     * <pre>
     * stream1: [0, 3, 1]
     * stream2: [2, 5, 6, 1]
     * selector: (a, b) -&gt; TAKE_SECOND
     * result: [2, 5, 6, 1, 0, 3, 1]
     * </pre>
     *
     * @param <T> the type of the elements
     * @param stream1  the first stream
     * @param stream2  the second stream
     * @param selector the selector function used to choose elements
     * @return the new stream
     * @throws NullPointerException if {@code stream1} or {@code stream2} is null
     * @since 1.1.9
     */
    public static <T> Stream<T> merge(Stream<? extends T> stream1, Stream<? extends T> stream2,
            BiFunction<? super T, ? super T, ObjMerge.MergeResult> selector) {
        Objects.requireNonNull(stream1);
        Objects.requireNonNull(stream2);
        return Stream.<T> merge(stream1.iterator, stream2.iterator, selector);
    }

    /**
     * Merges elements of two iterators according to the supplied selector function.
     *
     * <p>Example 1 — Merge two sorted iterators:
     * <pre>
     * iterator1: [1, 3, 8, 10]
     * iterator2: [2, 5, 6, 12]
     * selector: (a, b) -&gt; a &lt; b ? TAKE_FIRST : TAKE_SECOND
     * result: [1, 2, 3, 5, 6, 8, 10, 12]
     * </pre>
     *
     * <p>Example 2 — Concat two iterators:
     * <pre>
     * iterator1: [0, 3, 1]
     * iterator2: [2, 5, 6, 1]
     * selector: (a, b) -&gt; TAKE_SECOND
     * result: [2, 5, 6, 1, 0, 3, 1]
     * </pre>
     *
     * @param <T> the type of the elements
     * @param iterator1  the first iterator
     * @param iterator2  the second iterator
     * @param selector  the selector function used to choose elements
     * @return the new stream
     * @throws NullPointerException if {@code iterator1} or {@code iterator2} is null
     * @since 1.1.9
     */
    public static <T> Stream<T> merge(Iterator<? extends T> iterator1, Iterator<? extends T> iterator2,
            BiFunction<? super T, ? super T, ObjMerge.MergeResult> selector) {
        Objects.requireNonNull(iterator1);
        Objects.requireNonNull(iterator2);
        return new Stream<>(new ObjMerge<>(iterator1, iterator2, selector));
    }

    //<editor-fold defaultstate="collapsed" desc="Implementation">
    private final Iterator<T> iterator;
    private final Params params;

    private Stream(Iterator<? extends T> iterator) {
        this(null, iterator);
    }

    private Stream(Iterable<? extends T> iterable) {
        this(null, new LazyIterator<>(iterable));
    }

    private Stream(Params params, Iterable<? extends T> iterable) {
        this(params, new LazyIterator<>(iterable));
    }

    @SuppressWarnings("unchecked")
    Stream(Params params, Iterator<? extends T> iterator) {
        this.params = params;
        this.iterator = (Iterator<T>) iterator;
    }

    /**
     * Returns internal stream iterator.
     *
     * @return internal stream iterator
     */
    public Iterator<T> iterator() {
        return iterator;
    }

    /**
     * Returns {@code Stream} with elements that satisfy the given predicate.
     *
     * <p>This is an intermediate operation.
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
    public Stream<T> filter(final Predicate<? super T> predicate) {
        return new Stream<>(params, new ObjFilter<>(iterator, predicate));
    }

    /**
     * Returns {@code Stream} without null elements.
     *
     * <p>This is an intermediate operation.
     *
     * @return the new stream
     * @since 1.1.6
     */
    public Stream<T> skipNull() {
        return filter(Fn.notNull());
    }

    /**
     * Returns {@code Stream} with elements that obtained by applying the given function.
     *
     * <p>This is an intermediate operation.
     *
     * <p>Example:
     * <pre>
     * mapper: (a) -&gt; a + 5
     * stream: [1, 2, 3, 4]
     * result: [6, 7, 8, 9]
     * </pre>
     *
     * @param <R> the type of elements in resulting stream
     * @param mapper  the mapper function used to apply to each element
     * @return the new stream
     */
    public <R> Stream<R> map(final Function<? super T, ? extends R> mapper) {
        return new Stream<>(params, new ObjMap<>(iterator, mapper));
    }

    /**
     * Returns {@code IntStream} with elements that obtained by applying the given function.
     *
     * <p>This is an intermediate operation.
     *
     * @param mapper  the mapper function used to apply to each element
     * @return the new {@code IntStream}
     * @see #map(com.annimon.stream.function.Function)
     */
    public IntStream mapToInt(final ToIntFunction<? super T> mapper) {
        return new IntStream(params, new ObjMapToInt<>(iterator, mapper));
    }

    /**
     * Returns {@code LongStream} with elements that obtained by applying the given function.
     *
     * <p>This is an intermediate operation.
     *
     * @param mapper  the mapper function used to apply to each element
     * @return the new {@code LongStream}
     * @since 1.1.4
     * @see #map(com.annimon.stream.function.Function)
     */
    public LongStream mapToLong(final ToLongFunction<? super T> mapper) {
        return new LongStream(params, new ObjMapToLong<>(iterator, mapper));
    }

    /**
     * Returns {@code DoubleStream} with elements that obtained by applying the given function.
     *
     * <p>This is an intermediate operation.
     *
     * @param mapper  the mapper function used to apply to each element
     * @return the new {@code DoubleStream}
     * @since 1.1.4
     * @see #map(com.annimon.stream.function.Function)
     */
    public DoubleStream mapToDouble(final ToDoubleFunction<? super T> mapper) {
        return new DoubleStream(params, new ObjMapToDouble<>(iterator, mapper));
    }

    @SuppressWarnings("unchecked")
    public <K, V> EntryStream<K, V> mapToEntry(Function<? super T, Map.Entry<K, V>> mapper) {
        final Function<?, ?> identityMapper = Fn.identity();
        if (mapper == identityMapper) {
            return new EntryStream<>((Stream<Map.Entry<K, V>>) this);
        } else {
            return new EntryStream<>(this.map(mapper));
        }
    }

    public <K, V> EntryStream<K, V> mapToEntry(final Function<? super T, K> keyMapper, final Function<? super T, V> valueMapper) {
        return new EntryStream<>(this.map(new Function<T, Map.Entry<K, V>>() {
            @Override
            public Entry<K, V> apply(T t) {
                return new AbstractMap.SimpleImmutableEntry<>(keyMapper.apply(t), valueMapper.apply(t));
            }
        }));
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
     * @param <R> the type of elements in resulting stream
     * @param mapper  the mapper function used to apply to each element
     * @return the new stream
     */
    public <R> Stream<R> flatMap(final Function<? super T, ? extends Stream<? extends R>> mapper) {
        return new Stream<>(params, new ObjFlatMap<>(iterator, mapper));
    }

    public <R> Stream<R> flatMap2(final Function<? super T, ? extends Collection<? extends R>> mapper) {
        return flatMap(new Function<T, Stream<R>>() {
            @Override
            public Stream<R> apply(T t) {
                final Collection<? extends R> c = mapper.apply(t);
                return c == null || c.size() == 0 ? Stream.<R> empty() : Stream.of(c);
            }
        });
    }

    public <R> Stream<R> flatMap3(final Function<? super T, ? extends R[]> mapper) {
        return flatMap(new Function<T, Stream<R>>() {
            @Override
            public Stream<R> apply(T t) {
                final R[] a = mapper.apply(t);
                return a == null || a.length == 0 ? Stream.<R> empty() : Stream.of(a);
            }
        });
    }

    /**
     * Returns a stream consisting of the results of replacing each element of
     * this stream with the contents of a mapped stream produced by applying
     * the provided mapping function to each element.
     *
     * <p>This is an intermediate operation.
     *
     * @param mapper  the mapper function used to apply to each element
     * @return the new {@code IntStream}
     * @see #flatMap(com.annimon.stream.function.Function)
     */
    public IntStream flatMapToInt(final Function<? super T, ? extends IntStream> mapper) {
        return new IntStream(params, new ObjFlatMapToInt<>(iterator, mapper));
    }

    /**
     * Returns a stream consisting of the results of replacing each element of
     * this stream with the contents of a mapped stream produced by applying
     * the provided mapping function to each element.
     *
     * <p>This is an intermediate operation.
     *
     * @param mapper  the mapper function used to apply to each element
     * @return the new {@code LongStream}
     * @see #flatMap(com.annimon.stream.function.Function)
     */
    public LongStream flatMapToLong(final Function<? super T, ? extends LongStream> mapper) {
        return new LongStream(params, new ObjFlatMapToLong<>(iterator, mapper));
    }

    /**
     * Returns a stream consisting of the results of replacing each element of
     * this stream with the contents of a mapped stream produced by applying
     * the provided mapping function to each element.
     *
     * <p>This is an intermediate operation.
     *
     * @param mapper  the mapper function used to apply to each element
     * @return the new {@code DoubleStream}
     * @see #flatMap(com.annimon.stream.function.Function)
     */
    public DoubleStream flatMapToDouble(final Function<? super T, ? extends DoubleStream> mapper) {
        return new DoubleStream(params, new ObjFlatMapToDouble<>(iterator, mapper));
    }

    public <K, V> EntryStream<K, V> flatMapToEntry(Function<? super T, ? extends Stream<? extends Map.Entry<K, V>>> mapper) {
        return EntryStream.of(flatMap(mapper));
    }

    public <K, V> EntryStream<K, V> flatMapToEntry2(final Function<? super T, ? extends Map<K, V>> mapper) {
        final Function<T, Stream<Map.Entry<K, V>>> mapper2 = new Function<T, Stream<Map.Entry<K, V>>>() {
            @Override
            public Stream<Entry<K, V>> apply(T t) {
                return Stream.of(mapper.apply(t));
            }
        };

        return EntryStream.of(flatMap(mapper2));
    }

    /**
     * Returns {@code Stream} with indexed elements.
     * Indexing starts from 0 with step 1.
     *
     * <p>This is an intermediate operation.
     *
     * <p>Example:
     * <pre>
     * stream: ["a", "b", "c"]
     * result: [(0, "a"), (1, "b"), (2, "c")]
     * </pre>
     *
     * @return the new {@code IntPair} stream
     * @since 1.1.2
     */
    public Stream<Indexed<T>> indexed() {
        return map(new Function<T, Indexed<T>>() {
            private int index = 0;

            @Override
            public Indexed<T> apply(T t) {
                return new Indexed<>(index++, t);
            }
        });
    }

    /**
     * Returns {@code Stream} with distinct elements (as determined by {@code hashCode} and {@code equals} methods).
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
    public Stream<T> distinct() {
        return new Stream<>(params, new ObjDistinct<>(iterator));
    }

    /**
     * Returns {@code Stream} with distinct elements (as determined by {@code hashCode}
     * and {@code equals} methods) according to the given classifier function.
     *
     * <p>This is a stateful intermediate operation.
     *
     * <p>Example:
     * <pre>
     * classifier: (str) -&gt; str.length()
     * stream: ["a", "bc", "d", "ef", "ghij"]
     * result: ["a", "bc", "ghij"]
     * </pre>
     *
     * @param keyExtractor  the classifier function
     * @return the new stream
     * @since 1.1.8
     */
    public <K> Stream<T> distinctBy(Function<? super T, ? extends K> keyExtractor) {
        return new Stream<>(params, new ObjDistinctBy<>(iterator, keyExtractor));
    }

    /**
     * Returns {@code Stream} with sorted elements (as determinated by {@link Comparable} interface).
     *
     * <p>This is a stateful intermediate operation.
     * <p>If the elements of this stream are not {@link Comparable},
     * a {@code java.lang.ClassCastException} may be thrown when the terminal operation is executed.
     *
     * <p>Example:
     * <pre>
     * stream: [3, 4, 1, 2]
     * result: [1, 2, 3, 4]
     * </pre>
     *
     * @return the new stream
     */
    public Stream<T> sorted() {
        return sorted(new Comparator<T>() {

            @SuppressWarnings({ "unchecked", "rawtypes" })
            @Override
            public int compare(T o1, T o2) {
                Comparable c1 = (Comparable) o1;
                Comparable c2 = (Comparable) o2;
                return c1.compareTo(c2);
            }
        });
    }

    /**
     * Returns {@code Stream} with sorted elements (as determinated by provided {@code Comparator}).
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
     * @return the new stream
     */
    public Stream<T> sorted(final Comparator<? super T> comparator) {
        return new Stream<>(params, new ObjSorted<>(iterator, comparator));
    }

    /**
     * Returns {@code Stream} with sorted elements (as determinated by {@code Comparable} interface).
     * Each element transformed by given function {@code f} before comparing.
     *
     * <p>This is a stateful intermediate operation.
     *
     * <p>Example:
     * <pre>
     * f: (a) -&gt; -a
     * stream: [1, 2, 3, 4]
     * result: [4, 3, 2, 1]
     * </pre>
     *
     * @param <R> the type of the result of transforming function
     * @param keyExtractor  the transformation function
     * @return the new stream
     */
    public <U extends Comparable<? super U>> Stream<T> sortedBy(final Function<? super T, U> keyExtractor) {
        return sorted(Comparators.comparingBy(keyExtractor));
    }

    /**
     * Partitions {@code Stream} into {@code Map} entries according to the given classifier function.
     *
     * <p>This is a stateful intermediate operation.
     *
     * <p>Example:
     * <pre>
     * classifier: (str) -&gt; str.length()
     * stream: ["a", "bc", "d", "ef", "ghij"]
     * result: [{1: ["a", "d"]}, {2: ["bc", "ef"]}, {4: ["ghij"]}]
     * </pre>
     *
     * @param <K> the type of the keys, which are result of the classifier function
     * @param classifier  the classifier function
     * @return the new stream
     */
    public <K> Stream<Map.Entry<K, List<T>>> groupBy(final Function<? super T, ? extends K> classifier) {
        final Map<K, List<T>> map = collect(Collectors.<T, K> groupingBy(classifier));
        return new Stream<>(params, map.entrySet());
    }

    public <K, A, D> Stream<Map.Entry<K, D>> groupBy(Function<? super T, ? extends K> classifier, Collector<? super T, A, D> downstream) {
        final Map<K, D> map = collect(Collectors.groupingBy(classifier, downstream));
        return new Stream<>(params, map.entrySet());
    }

    public <K, A, D> Stream<Map.Entry<K, D>> groupBy(final Function<? super T, ? extends K> classifier, final Collector<? super T, A, D> downstream,
            final Supplier<Map<K, D>> mapFactory) {
        final Map<K, D> map = collect(Collectors.groupingBy(classifier, downstream, mapFactory));

        return new Stream<>(params, map.entrySet());
    }

    public <K> EntryStream<K, List<T>> groupByToEntry(final Function<? super T, ? extends K> classifier) {
        final Function<Map.Entry<K, List<T>>, Map.Entry<K, List<T>>> mapper = Fn.identity();
        @SuppressWarnings("unchecked")
        final Function<T, K> classifier2 = (Function<T, K>) classifier;

        return groupBy(classifier2).mapToEntry(mapper);
    }

    public <K, A, D> EntryStream<K, D> groupByToEntry(Function<? super T, ? extends K> classifier, Collector<? super T, A, D> downstream) {
        final Function<Map.Entry<K, D>, Map.Entry<K, D>> mapper = Fn.identity();
        @SuppressWarnings("unchecked")
        final Function<T, K> classifier2 = (Function<T, K>) classifier;

        return groupBy(classifier2, downstream).mapToEntry(mapper);
    }

    public <K, A, D> EntryStream<K, D> groupByToEntry(final Function<? super T, ? extends K> classifier, final Collector<? super T, A, D> downstream,
            final Supplier<Map<K, D>> mapFactory) {
        final Function<Map.Entry<K, D>, Map.Entry<K, D>> mapper = Fn.identity();
        @SuppressWarnings("unchecked")
        final Function<T, K> classifier2 = (Function<T, K>) classifier;

        return groupBy(classifier2, downstream, mapFactory).mapToEntry(mapper);
    }

    /**
     * Partitions {@code Stream} into {@code List}s according to the given classifier function. In contrast
     * to {@link #groupBy(Function)}, this method assumes that the elements of the stream are sorted.
     * Because of this assumption, it does not need to first collect all elements and then partition them.
     * Instead, it can emit a {@code List} of elements when it reaches the first element that does not
     * belong to the same chunk as the previous elements.
     *
     * <p>This is an intermediate operation.
     *
     * <p>Example:
     * <pre>
     * classifier: (a) -&gt; a % 5 == 0
     * stream: [1, 2, 5, 6, 7, 9, 10, 12, 14]
     * result: [[1, 2], [5], [6, 7, 9], [10], [12, 14]]
     * </pre>
     *
     * @param <K> the type of the keys, which are the result of the classifier function
     * @param classifier  the classifier function
     * @return the new stream
     */
    public <K> Stream<List<T>> chunkBy(final Function<? super T, ? extends K> classifier) {
        return new Stream<>(params, new ObjChunkBy<>(iterator, classifier));
    }

    public Stream<List<T>> split(final int size) {
        return sliding(size, size);
    }

    /**
     * Partitions {@code Stream} into {@code List}s of fixed size by sliding over the elements of the stream.
     * It starts with the first element and in each iteration moves by 1. This method yields the same results
     * as calling {@link #sliding(int, int)} with a {@code stepWidth} of 1.
     *
     * <p>This is an intermediate operation.
     *
     * <p>Example:
     * <pre>
     * windowSize: 3
     * stream: [1, 2, 3, 4, 5]
     * result: [[1, 2, 3], [2, 3, 4], [3, 4, 5]]
     * </pre>
     *
     * @param windowSize  number of elements that will be emitted together in a list
     * @return the new stream
     * @see #sliding(int, int)
     */
    public Stream<List<T>> sliding(final int windowSize) {
        return sliding(windowSize, 1);
    }

    /**
     * Partitions {@code Stream} into {@code List}s of fixed size by sliding over the elements of the stream.
     * It starts with the first element and in each iteration moves by the given step width. This method
     * allows, for example, to partition the elements into batches of {@code windowSize} elements (by using a
     * step width equal to the specified window size) or to sample every n-th element (by using a window size
     * of 1 and a step width of n).
     *
     * <p>This is an intermediate operation.
     *
     * <p>Example:
     * <pre>
     * windowSize: 3, stepWidth: 3
     * stream: [1, 1, 1, 2, 2, 2, 3, 3, 3]
     * result: [[1, 1, 1], [2, 2, 2] [3, 3, 3]]
     *
     * windowSize: 2, stepWidth: 3
     * stream: [1, 2, 3, 1, 2, 3, 1, 2, 3]
     * result: [[1, 2], [1, 2], [1, 2]]
     *
     * windowSize: 3, stepWidth: 1
     * stream: [1, 2, 3, 4, 5, 6]
     * result: [[1, 2, 3], [2, 3, 4], [3, 4, 5], [4, 5, 6]]
     * </pre>
     *
     * @param windowSize  number of elements that will be emitted together in a list
     * @param stepWidth  step width
     * @return the new stream
     * @throws IllegalArgumentException if {@code windowSize} is zero or negative
     * @throws IllegalArgumentException if {@code stepWidth} is zero or negative
     */
    public Stream<List<T>> sliding(final int windowSize, final int stepWidth) {
        if (windowSize <= 0)
            throw new IllegalArgumentException("windowSize cannot be zero or negative");
        if (stepWidth <= 0)
            throw new IllegalArgumentException("stepWidth cannot be zero or negative");
        return new Stream<>(params, new ObjSlidingWindow<>(iterator, windowSize, stepWidth));
    }

    /**
     * Performs provided action on each element.
     *
     * <p>This is an intermediate operation.
     *
     * @param action  the action to be performed on each element
     * @return the new stream
     */
    public Stream<T> peek(final Consumer<? super T> action) {
        return new Stream<>(params, new ObjPeek<>(iterator, action));
    }

    /**
     * Returns a {@code Stream} produced by iterative application of a accumulation function
     * to reduction value and next element of the current stream.
     * Produces a {@code Stream} consisting of {@code value1}, {@code acc(value1, value2)},
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
    public Stream<T> scan(final BiFunction<T, T, T> accumulator) {
        Objects.requireNonNull(accumulator);
        return new Stream<>(params, new ObjScan<>(iterator, accumulator));
    }

    /**
     * Returns a {@code Stream} produced by iterative application of a accumulation function
     * to an initial element {@code identity} and next element of the current stream.
     * Produces a {@code Stream} consisting of {@code identity}, {@code acc(identity, value1)},
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
     * @param <R> the type of the result
     * @param identity  the initial value
     * @param accumulator  the accumulation function
     * @return the new stream
     * @throws NullPointerException if {@code accumulator} is null
     * @since 1.1.6
     */
    public <R> Stream<R> scan(final R identity, final BiFunction<? super R, ? super T, ? extends R> accumulator) {
        Objects.requireNonNull(accumulator);
        return new Stream<>(params, new ObjScanIdentity<>(iterator, identity, accumulator));
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
     * @return the new stream
     */
    public Stream<T> takeWhile(final Predicate<? super T> predicate) {
        return new Stream<>(params, new ObjTakeWhile<>(iterator, predicate));
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
     * @return the new stream
     * @since 1.1.6
     */
    public Stream<T> takeUntil(final Predicate<? super T> stopPredicate) {
        return new Stream<>(params, new ObjTakeUntil<>(iterator, stopPredicate));
    }

    /**
     * Drops elements while the predicate is true, then returns the rest.
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
     * @return the new stream
     */
    public Stream<T> dropWhile(final Predicate<? super T> predicate) {
        return new Stream<>(params, new ObjDropWhile<>(iterator, predicate));
    }

    /**
     * Returns {@code Stream} with first {@code maxSize} elements.
     *
     * <p>This is a short-circuiting stateful intermediate operation.
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
     * @param maxSize  the number of elements to limit
     * @return the new stream
     * @throws IllegalArgumentException if {@code maxSize} is negative
     */
    public Stream<T> limit(final long maxSize) {
        if (maxSize < 0) {
            throw new IllegalArgumentException("maxSize cannot be negative");
        }
        if (maxSize == 0) {
            return Stream.empty();
        }
        return new Stream<>(params, new ObjLimit<>(iterator, maxSize));
    }

    /**
     * Skips first {@code n} elements and returns {@code Stream} with remaining elements.
     * If stream contains fewer than {@code n} elements, then an empty stream will be returned.
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
    public Stream<T> skip(final long n) {
        if (n < 0)
            throw new IllegalArgumentException("n cannot be negative");
        if (n == 0)
            return this;
        return new Stream<>(params, new ObjSkip<>(iterator, n));
    }

    /**
     * Tests whether any elements match the given predicate.
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
     * @return {@code true} if any elements match the given predicate, otherwise {@code false}
     */
    public boolean anyMatch(Predicate<? super T> predicate) {
        return match(predicate, MATCH_ANY);
    }

    /**
     * Tests whether all elements match the given predicate.
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
     * @return {@code true} if all elements match the given predicate, otherwise {@code false}
     */
    public boolean allMatch(Predicate<? super T> predicate) {
        return match(predicate, MATCH_ALL);
    }

    /**
     * Tests whether no elements match the given predicate.
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
     * @return {@code true} if no elements match the given predicate, otherwise {@code false}
     */
    public boolean noneMatch(Predicate<? super T> predicate) {
        return match(predicate, MATCH_NONE);
    }

    /**
     * Returns the first element wrapped by {@code Optional} class.
     * If stream is empty, returns {@code Optional.empty()}.
     *
     * <p>This is a short-circuiting terminal operation.
     *
     * @return an {@code Optional} with the first element
     *         or {@code Optional.empty()} if stream is empty
     */
    public Optional<T> findFirst() {
        if (iterator.hasNext()) {
            return Optional.<T> of(iterator.next());
        }
        return Optional.empty();
    }

    /**
     * Returns the last element wrapped by {@code Optional} class.
     * If stream is empty, returns {@code Optional.empty()}.
     *
     * <p>This is a short-circuiting terminal operation.
     *
     * @return an {@code Optional} with the last element
     *         or {@code Optional.empty()} if the stream is empty
     * @since 1.1.8
     */
    public Optional<T> findLast() {
        return reduce(new BinaryOperator<T>() {
            @Override
            public T apply(T left, T right) {
                return right;
            }
        });
    }

    /**
     * Finds the minimum element according to the given comparator.
     *
     * <p>This is a terminal operation.
     *
     * <p>Example:
     * <pre>
     * comparator: (a, b) -&gt; a.compareTo(b)
     * stream: [1, 2, 3, 4, 5]
     * result: 1
     * </pre>
     *
     * @param comparator  the {@code Comparator} to compare elements
     * @return the minimum element
     */
    public Optional<T> min(Comparator<? super T> comparator) {
        return reduce(Fn.<T> minBy(comparator));
    }

    public <U extends Comparable<? super U>> Optional<T> minBy(final Function<? super T, U> keyExtractor) {
        return min(Comparators.comparingBy(keyExtractor));
    }

    /**
     * Finds the maximum element according to the given comparator.
     *
     * <p>This is a terminal operation.
     *
     * <p>Example:
     * <pre>
     * comparator: (a, b) -&gt; a.compareTo(b)
     * stream: [1, 2, 3, 4, 5]
     * result: 5
     * </pre>
     *
     * @param comparator  the {@code Comparator} to compare elements
     * @return the maximum element
     */
    public Optional<T> max(Comparator<? super T> comparator) {
        return reduce(Fn.<T> maxBy(comparator));
    }

    public <U extends Comparable<? super U>> Optional<T> maxBy(final Function<? super T, U> keyExtractor) {
        return max(Comparators.comparingBy(keyExtractor));
    }

    /**
     * Performs the given action on each element.
     *
     * <p>This is a terminal operation.
     *
     * @param action  the action to be performed on each element
     */
    public void forEach(final Consumer<? super T> action) {
        while (iterator.hasNext()) {
            action.accept(iterator.next());
        }
    }

    /**
     * Reduces the elements using provided identity value and the associative accumulation function.
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
     * @param <R> the type of the result
     * @param identity  the initial value
     * @param accumulator  the accumulation function
     * @return the result of the reduction
     */
    public <R> R reduce(R identity, BiFunction<? super R, ? super T, ? extends R> accumulator) {
        R result = identity;
        while (iterator.hasNext()) {
            final T value = iterator.next();
            result = accumulator.apply(result, value);
        }
        return result;
    }

    /**
     * Reduces the elements using provided associative accumulation function.
     *
     * <p>This is a terminal operation.
     *
     * @param accumulator  the accumulation function
     * @return the result of the reduction
     * @see #reduce(java.lang.Object, com.annimon.stream.function.BiFunction)
     */
    public Optional<T> reduce(BiFunction<T, T, T> accumulator) {
        boolean foundAny = false;
        T result = null;
        while (iterator.hasNext()) {
            final T value = iterator.next();
            if (!foundAny) {
                foundAny = true;
                result = value;
            } else {
                result = accumulator.apply(result, value);
            }
        }
        return foundAny ? Optional.of(result) : Optional.<T> empty();
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
     * @see #collect(com.annimon.stream.Collector)
     */
    public <R> R collect(Supplier<R> supplier, BiConsumer<R, ? super T> accumulator) {
        final R result = supplier.get();
        while (iterator.hasNext()) {
            final T value = iterator.next();
            accumulator.accept(result, value);
        }
        return result;
    }

    /**
     * Collects elements with {@code collector} that encapsulates supplier, accumulator and combiner functions.
     *
     * <p>This is a terminal operation.
     *
     * @param <R> the type of result
     * @param <A> the intermediate used by {@code Collector}
     * @param collector  the {@code Collector}
     * @return the result of collect elements
     * @see #collect(com.annimon.stream.function.Supplier, com.annimon.stream.function.BiConsumer)
     */
    public <R, A> R collect(Collector<? super T, A, R> collector) {
        A container = collector.supplier().get();
        while (iterator.hasNext()) {
            final T value = iterator.next();
            collector.accumulator().accept(container, value);
        }
        if (collector.finisher() != null)
            return collector.finisher().apply(container);
        return Collectors.<A, R> castIdentity().apply(container);
    }

    /**
     * Returns the count of elements in this stream.
     *
     * <p>This is a terminal operation.
     *
     * @return the count of elements
     */
    public int count() {
        int count = 0;
        while (iterator.hasNext()) {
            iterator.next();
            count++;
        }
        return count;
    }

    /**
     * Collects elements to an array.
     *
     * <p>This is a terminal operation.
     *
     * @return the result of collect elements
     * @see #toArray(com.annimon.stream.function.IntFunction)
     */
    public Object[] toArray() {
        return toArray(new IntFunction<Object[]>() {

            @Override
            public Object[] apply(int value) {
                return new Object[value];
            }
        });
    }

    /**
     * Collects elements to an array, the {@code generator} constructor of provided.
     *
     * <p>This is a terminal operation.
     *
     * @param <R> the type of the result
     * @param generator  the array constructor reference that accommodates future array of assigned size
     * @return the result of collect elements
     */
    public <R> R[] toArray(IntFunction<R[]> generator) {
        return Operators.toArray(iterator, generator);
    }

    /**
     * Collects elements to a new {@code List}.
     *
     * <p>This implementation <strong>does not</strong> call {@code collect(Collectors.toList())}, so
     * it can be faster by reducing method calls.
     *
     * <p>This is a terminal operation.
     *
     * @return a new {@code List}
     * @since 1.1.5
     * @see Collectors#toList()
     */
    public List<T> toList() {
        final List<T> result = new ArrayList<>();
        while (iterator.hasNext()) {
            result.add(iterator.next());
        }
        return result;
    }

    public Set<T> toSet() {
        final Set<T> result = new HashSet<>();
        while (iterator.hasNext()) {
            result.add(iterator.next());
        }
        return result;
    }

    public <C extends Collection<T>> C toCollection(Supplier<C> supplier) {
        final C result = supplier.get();
        while (iterator.hasNext()) {
            result.add(iterator.next());
        }
        return result;
    }

    /**
     * 
     * @param keyMapper
     * @return
     * @see Collectors#toMap(Function)
     */
    public <K> Map<K, T> toMap(final Function<? super T, ? extends K> keyMapper) {
        return collect(Collectors.toMap(keyMapper));
    }

    /**
     * 
     * @param keyMapper
     * @param valueMapper
     * @return
     * @see Collectors#toMap(Function, Function)
     */
    public <K, V> Map<K, V> toMap(final Function<? super T, ? extends K> keyMapper, final Function<? super T, ? extends V> valueMapper) {
        return collect(Collectors.toMap(keyMapper, valueMapper));
    }

    /**
     * 
     * @param keyMapper
     * @param valueMapper
     * @param mapFactory
     * @return
     * @see Collectors#toMap(Function, Function, Supplier)
     */
    public <K, V, M extends Map<K, V>> M toMap(final Function<? super T, ? extends K> keyMapper, final Function<? super T, ? extends V> valueMapper,
            final Supplier<M> mapFactory) {
        return collect(Collectors.toMap(keyMapper, valueMapper, mapFactory));
    }

    /**
     * Applies custom operator on stream.
     *
     * Transforming function can return {@code Stream} for intermediate operations,
     * or any value for terminal operation.
     *
     * <p>Operator examples:
     * <pre><code>
     *     // Intermediate operator
     *     public class Reverse&lt;T&gt; implements Function&lt;Stream&lt;T&gt;, Stream&lt;T&gt;&gt; {
     *         &#64;Override
     *         public Stream&lt;T&gt; apply(Stream&lt;T&gt; stream) {
     *             final Iterator&lt;? extends T&gt; iterator = stream.iterator();
     *             final ArrayDeque&lt;T&gt; deque = new ArrayDeque&lt;T&gt;();
     *             while (iterator.hasNext()) {
     *                 deque.addFirst(iterator.next());
     *             }
     *             return Stream.of(deque.iterator());
     *         }
     *     }
     *
     *     // Intermediate operator based on existing stream operators
     *     public class SkipAndLimit&lt;T&gt; implements UnaryOperator&lt;Stream&lt;T&gt;&gt; {
     *
     *         private final int skip, limit;
     *
     *         public SkipAndLimit(int skip, int limit) {
     *             this.skip = skip;
     *             this.limit = limit;
     *         }
     *
     *         &#64;Override
     *         public Stream&lt;T&gt; apply(Stream&lt;T&gt; stream) {
     *             return stream.skip(skip).limit(limit);
     *         }
     *     }
     *
     *     // Terminal operator
     *     public class Sum implements Function&lt;Stream&lt;Integer&gt;, Integer&gt; {
     *         &#64;Override
     *         public Integer apply(Stream&lt;Integer&gt; stream) {
     *             return stream.reduce(0, new BinaryOperator&lt;Integer&gt;() {
     *                 &#64;Override
     *                 public Integer apply(Integer value1, Integer value2) {
     *                     return value1 + value2;
     *                 }
     *             });
     *         }
     *     }
     * </code></pre>
     *
     * @param <R> the type of the result
     * @param function  a transforming function
     * @return a result of the transforming function
     * @throws NullPointerException if {@code function} is null
     */
    public <R> R chain(Function<Stream<T>, R> function) {
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
    public Stream<T> onClose(final Runnable closeHandler) {
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
        return new Stream<>(newParams, iterator);
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

    private static final int MATCH_ANY = 0;
    private static final int MATCH_ALL = 1;
    private static final int MATCH_NONE = 2;

    private boolean match(Predicate<? super T> predicate, int matchKind) {
        final boolean kindAny = (matchKind == MATCH_ANY);
        final boolean kindAll = (matchKind == MATCH_ALL);

        while (iterator.hasNext()) {
            final T value = iterator.next();

            /*if (predicate.test(value)) {
                // anyMatch -> true
                // noneMatch -> false
                if (!kindAll) {
                    return matchAny;
                }
            } else {
                // allMatch -> false
                if (kindAll) {
                    return false;
                }
            }*/
            // match && !kindAll -> kindAny
            // !match && kindAll -> false
            final boolean match = predicate.test(value);
            if (match ^ kindAll) {
                return kindAny && match; // (match ? kindAny : false);
            }
        }
        // anyMatch -> false
        // allMatch -> true
        // noneMatch -> true
        return !kindAny;
    }
    //</editor-fold>
}
