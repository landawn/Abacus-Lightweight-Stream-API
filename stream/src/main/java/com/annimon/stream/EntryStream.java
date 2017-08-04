/** <a href="http://www.cpupk.com/decompiler">Eclipse Class Decompiler</a> plugin, Copyright (c) 2017 Chen Chao. */
/*
 * Copyright (C) 2017 HaiYang Li
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.annimon.stream;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.annimon.stream.function.BiConsumer;
import com.annimon.stream.function.BiFunction;
import com.annimon.stream.function.BiPredicate;
import com.annimon.stream.function.BinaryOperator;
import com.annimon.stream.function.Consumer;
import com.annimon.stream.function.Function;
import com.annimon.stream.function.Predicate;
import com.annimon.stream.function.Supplier;

/**
 * 
 * @since 0.9
 * 
 * @author Haiyang Li
 */
public final class EntryStream<K, V> {

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static final EntryStream EMPTY = of(new Map.Entry[0]);

    private final Stream<Map.Entry<K, V>> s;

    @SuppressWarnings("unchecked")
    EntryStream(final Stream<? extends Map.Entry<K, V>> s) {
        this.s = (Stream<Map.Entry<K, V>>) s;
    }

    @SuppressWarnings("unchecked")
    public static <K, V> EntryStream<K, V> empty() {
        return EMPTY;
    }

    public static <K, V> EntryStream<K, V> of(final Stream<? extends Map.Entry<K, V>> s) {
        return new EntryStream<>(s);
    }

    public static <K, V> EntryStream<K, V> of(final Iterator<? extends Map.Entry<K, V>> iterator) {
        return new EntryStream<>(Stream.of(iterator));
    }

    public static <K, V> EntryStream<K, V> of(final Map<K, V> map) {
        return new EntryStream<>(Stream.of(map));
    }

    public static <K, V> EntryStream<K, V> of(final Collection<? extends Map.Entry<K, V>> entries) {
        return new EntryStream<>(Stream.of(entries));
    }

    @SafeVarargs
    public static <K, V> EntryStream<K, V> of(final Map.Entry<K, V>... entries) {
        return new EntryStream<>(Stream.of(entries));
    }

    public static <K, T> EntryStream<K, T> of(final Collection<? extends T> c, final Function<? super T, K> keyExtractor) {
        final Function<T, T> valueMapper = Fn.identity();

        return Stream.of(c).mapToEntry(keyExtractor, valueMapper);
    }

    public static <K, T> EntryStream<K, T> of(final T[] a, final Function<? super T, K> keyExtractor) {
        final Function<T, T> valueMapper = Fn.identity();

        return Stream.of(a).mapToEntry(keyExtractor, valueMapper);
    }

    @SafeVarargs
    public static <K, V> EntryStream<K, V> concat(final Map<K, V>... maps) {
        final Function<Map<K, V>, Map<K, V>> mapper = Fn.identity();

        return Stream.of(maps).flatMapToEntry2(mapper);
    }

    public static <K, V> EntryStream<K, V> concat(final Collection<? extends Map<K, V>> maps) {
        final Function<Map<K, V>, Map<K, V>> mapper = Fn.identity();

        return Stream.of(maps).flatMapToEntry2(mapper);
    }

    public static <K, V> EntryStream<K, V> zip(final K[] keys, final V[] values) {
        final BiFunction<K, V, Map.Entry<K, V>> zipFunction = new BiFunction<K, V, Map.Entry<K, V>>() {
            @Override
            public Entry<K, V> apply(K k, V v) {
                return new AbstractMap.SimpleImmutableEntry<>(k, v);
            }
        };

        final Function<Map.Entry<K, V>, Map.Entry<K, V>> mapper = Fn.identity();

        return Stream.zip(Stream.of(keys), Stream.of(values), zipFunction).mapToEntry(mapper);
    }

    public static <K, V> EntryStream<K, V> zip(final Collection<? extends K> keys, final Collection<? extends V> values) {
        final BiFunction<K, V, Map.Entry<K, V>> zipFunction = new BiFunction<K, V, Map.Entry<K, V>>() {
            @Override
            public Entry<K, V> apply(K k, V v) {
                return new AbstractMap.SimpleImmutableEntry<>(k, v);
            }
        };

        final Function<Map.Entry<K, V>, Map.Entry<K, V>> mapper = Fn.identity();

        return Stream.zip(Stream.of(keys), Stream.of(values), zipFunction).mapToEntry(mapper);
    }

    public Stream<K> keys() {
        final Function<Map.Entry<K, V>, K> func = Fn.key();

        return s.map(func);
    }

    public Stream<V> values() {
        final Function<Map.Entry<K, V>, V> func = Fn.value();

        return s.map(func);
    }

    public Stream<Map.Entry<K, V>> entries() {
        return s;
    }

    public EntryStream<V, K> inversed() {
        final Function<Map.Entry<K, V>, Map.Entry<V, K>> mapper = new Function<Map.Entry<K, V>, Map.Entry<V, K>>() {
            @Override
            public Entry<V, K> apply(Entry<K, V> e) {
                return new AbstractMap.SimpleImmutableEntry<>(e.getValue(), e.getKey());
            }
        };

        return map(mapper);
    }

    public <KK> EntryStream<K, V> filter(final Predicate<Map.Entry<K, V>> predicate) {
        return of(s.filter(predicate));
    }

    public <KK> EntryStream<K, V> filter(final BiPredicate<? super K, ? super V> predicate) {
        final Predicate<Map.Entry<K, V>> predicate2 = new Predicate<Map.Entry<K, V>>() {
            @Override
            public boolean test(Entry<K, V> entry) {
                return predicate.test(entry.getKey(), entry.getValue());
            }
        };

        return of(s.filter(predicate2));
    }

    public <KK> EntryStream<K, V> filterByKey(final Predicate<? super K> keyPredicate) {
        final Predicate<Map.Entry<K, V>> predicate = Fn.testByKey(keyPredicate);

        return of(s.filter(predicate));
    }

    public <KK> EntryStream<K, V> filterByValue(final Predicate<? super V> valuePredicate) {
        final Predicate<Map.Entry<K, V>> predicate = Fn.testByValue(valuePredicate);

        return of(s.filter(predicate));
    }

    public <KK, VV> EntryStream<KK, VV> map(final Function<? super Map.Entry<K, V>, Map.Entry<KK, VV>> mapper) {
        return of(s.map(mapper));
    }

    public <KK, VV> EntryStream<KK, VV> map(final BiFunction<? super K, ? super V, Map.Entry<KK, VV>> mapper) {
        final Function<Map.Entry<K, V>, Map.Entry<KK, VV>> mapper2 = new Function<Map.Entry<K, V>, Map.Entry<KK, VV>>() {
            @Override
            public Entry<KK, VV> apply(Map.Entry<K, V> entry) {
                return mapper.apply(entry.getKey(), entry.getValue());
            }
        };

        return of(s.map(mapper2));
    }

    public <KK, VV> EntryStream<KK, VV> map(final Function<? super K, KK> keyMapper, final Function<? super V, VV> valueMapper) {
        final Function<Map.Entry<K, V>, Map.Entry<KK, VV>> mapper = new Function<Map.Entry<K, V>, Map.Entry<KK, VV>>() {
            @Override
            public Entry<KK, VV> apply(Entry<K, V> t) {
                return new AbstractMap.SimpleImmutableEntry<>(keyMapper.apply(t.getKey()), valueMapper.apply(t.getValue()));
            }
        };

        return map(mapper);
    }

    public <KK> EntryStream<KK, V> mapKey(final Function<? super K, KK> keyMapper) {
        final Function<Map.Entry<K, V>, Map.Entry<KK, V>> mapper = Fn.mapKey(keyMapper);

        return of(s.map(mapper));
    }

    public <VV> EntryStream<K, VV> mapValue(final Function<? super V, VV> valueMapper) {
        final Function<Map.Entry<K, V>, Map.Entry<K, VV>> mapper = Fn.mapValue(valueMapper);

        return of(s.map(mapper));
    }

    public <KK, VV> EntryStream<KK, VV> flatMap(final Function<? super Map.Entry<K, V>, EntryStream<KK, VV>> mapper) {
        final Function<Map.Entry<K, V>, Stream<Map.Entry<KK, VV>>> mapper2 = new Function<Map.Entry<K, V>, Stream<Map.Entry<KK, VV>>>() {
            @Override
            public Stream<Entry<KK, VV>> apply(Entry<K, V> t) {
                return mapper.apply(t).s;
            }
        };

        return flatMap2(mapper2);
    }

    public <KK, VV> EntryStream<KK, VV> flatMap2(final Function<? super Map.Entry<K, V>, Stream<Map.Entry<KK, VV>>> mapper) {
        return of(s.flatMap(mapper));
    }

    public <KK, VV> EntryStream<KK, VV> flatMap3(final Function<? super Map.Entry<K, V>, Map<KK, VV>> mapper) {
        final Function<Map.Entry<K, V>, Stream<Map.Entry<KK, VV>>> mapper2 = new Function<Map.Entry<K, V>, Stream<Map.Entry<KK, VV>>>() {
            @Override
            public Stream<Entry<KK, VV>> apply(Entry<K, V> t) {
                return Stream.of(mapper.apply(t));
            }
        };

        return flatMap2(mapper2);
    }

    public <KK> EntryStream<KK, V> flatMapKey(final Function<? super K, Stream<KK>> keyMapper) {
        final Function<Map.Entry<K, V>, Stream<Map.Entry<KK, V>>> mapper2 = new Function<Map.Entry<K, V>, Stream<Map.Entry<KK, V>>>() {
            @Override
            public Stream<Entry<KK, V>> apply(final Map.Entry<K, V> e) {
                return keyMapper.apply(e.getKey()).map(new Function<KK, Map.Entry<KK, V>>() {
                    @Override
                    public Map.Entry<KK, V> apply(KK kk) {
                        return new AbstractMap.SimpleImmutableEntry<>(kk, e.getValue());
                    }
                });
            }
        };

        return flatMap2(mapper2);
    }

    public <VV> EntryStream<K, VV> flatMapValue(final Function<? super V, Stream<VV>> valueMapper) {
        final Function<Map.Entry<K, V>, Stream<Map.Entry<K, VV>>> mapper2 = new Function<Map.Entry<K, V>, Stream<Map.Entry<K, VV>>>() {
            @Override
            public Stream<Entry<K, VV>> apply(final Entry<K, V> e) {
                return valueMapper.apply(e.getValue()).map(new Function<VV, Map.Entry<K, VV>>() {
                    @Override
                    public Map.Entry<K, VV> apply(VV vv) {
                        return new AbstractMap.SimpleImmutableEntry<>(e.getKey(), vv);
                    }
                });
            }
        };

        return flatMap2(mapper2);
    }

    /**
     * 
     * @param classifier
     * @return
     * @see Collectors#groupingBy(Function)
     */
    public EntryStream<K, List<V>> groupBy() {
        final Function<? super Map.Entry<K, V>, K> classifier = Fn.key();
        final Function<? super Map.Entry<K, V>, V> valueMapper = Fn.value();
        final Collector<Entry<K, V>, ?, List<V>> collector = Collectors.mapping(valueMapper, Collectors.<V> toList());

        return of(s.groupBy(classifier, collector));
    }

    /**
     * 
     * @param downstream
     * @return
     * @see Collectors#groupingBy(Function, Collector)
     */
    public <A, D> EntryStream<K, D> groupBy(final Collector<? super Map.Entry<K, V>, A, D> downstream) {
        final Function<? super Map.Entry<K, V>, K> classifier = Fn.key();

        return of(s.groupBy(classifier, downstream));
    }

    /**
     * 
     * @param downstream
     * @param mapFactory
     * @return
     * @see Collectors#groupingBy(Function, Collector)
     */
    public <A, D> EntryStream<K, D> groupBy(final Collector<? super Map.Entry<K, V>, A, D> downstream, final Supplier<Map<K, D>> mapFactory) {
        final Function<? super Map.Entry<K, V>, K> classifier = Fn.key();

        return of(s.groupBy(classifier, downstream, mapFactory));
    }

    /**
     * 
     * @param classifier
     * @param downstream
     * @param mapFactory
     * @return
     * @see Collectors#groupingBy(Function, Collector, Supplier)
     */
    public <KK, A, D> EntryStream<KK, D> groupBy(final Function<? super Map.Entry<K, V>, ? extends KK> classifier,
            final Collector<? super Map.Entry<K, V>, A, D> downstream) {

        return of(s.groupBy(classifier, downstream));
    }

    /**
     * 
     * @param classifier
     * @param downstream
     * @return
     * @see Collectors#groupingBy(Function, Collector)
     */
    public <KK, A, D> EntryStream<KK, D> groupBy(final Function<? super Map.Entry<K, V>, ? extends KK> classifier,
            final Collector<? super Map.Entry<K, V>, A, D> downstream, final Supplier<Map<KK, D>> mapFactory) {

        return of(s.groupBy(classifier, downstream, mapFactory));
    }

    public EntryStream<K, V> sorted(final Comparator<? super Map.Entry<K, V>> comparator) {
        return of(s.sorted(comparator));
    }

    public EntryStream<K, V> sortedByKey(final Comparator<? super K> keyComparator) {
        final Comparator<Map.Entry<K, V>> comparator = Comparators.comparingByKey(keyComparator);

        return of(s.sorted(comparator));
    }

    public EntryStream<K, V> sortedByValue(final Comparator<? super V> valueComparator) {
        final Comparator<Map.Entry<K, V>> comparator = Comparators.comparingByValue(valueComparator);

        return of(s.sorted(comparator));
    }

    public <U extends Comparable<? super U>> EntryStream<K, V> sortedBy(final Function<? super Map.Entry<K, V>, U> keyExtractor) {
        return of(s.sortedBy(keyExtractor));
    }

    public EntryStream<K, V> distinct() {
        return of(s.distinct());
    }

    public EntryStream<K, V> distinctByKey() {
        final Function<? super Entry<K, V>, K> keyExtractor = Fn.key();

        return of(s.distinctBy(keyExtractor));
    }

    public EntryStream<K, V> distinctByValue() {
        final Function<? super Entry<K, V>, V> keyExtractor = Fn.value();

        return of(s.distinctBy(keyExtractor));
    }

    public EntryStream<K, V> distinctBy(final Function<? super Map.Entry<K, V>, ?> keyExtractor) {
        return of(s.distinctBy(keyExtractor));
    }

    public EntryStream<K, V> skip(long n) {
        return of(s.skip(n));
    }

    public EntryStream<K, V> limit(long n) {
        return of(s.limit(n));
    }

    public EntryStream<K, V> peek(final Consumer<? super Map.Entry<K, V>> action) {
        return of(s.peek(action));
    }

    public EntryStream<K, V> peek(final BiConsumer<? super K, ? super V> action) {
        final Consumer<Map.Entry<K, V>> action2 = new Consumer<Map.Entry<K, V>>() {
            @Override
            public void accept(Entry<K, V> entry) {
                action.accept(entry.getKey(), entry.getValue());
            }
        };

        return of(s.peek(action2));
    }

    public void forEach(final Consumer<? super Map.Entry<K, V>> action) {
        s.forEach(action);
    }

    public void forEach(final BiConsumer<? super K, ? super V> action) {
        final Consumer<Map.Entry<K, V>> action2 = new Consumer<Map.Entry<K, V>>() {
            @Override
            public void accept(Entry<K, V> entry) {
                action.accept(entry.getKey(), entry.getValue());
            }
        };

        s.forEach(action2);
    }

    public int count() {
        return s.count();
    }

    public Iterator<Map.Entry<K, V>> iterator() {
        return s.iterator();
    }

    /**
     * 
     * @return
     */
    public Map<K, V> toMap() {
        final Function<? super Map.Entry<K, V>, K> classifier = Fn.key();
        final Function<? super Map.Entry<K, V>, V> valueMapper = Fn.value();

        return toMap(classifier, valueMapper);
    }

    /**
     * 
     * @param keyExtractor
     * @param valueMapper
     * @return
     * @see Collectors#toMap(Function, Function)
     */
    public <KK, VV> Map<KK, VV> toMap(final Function<? super Map.Entry<K, V>, ? extends KK> keyExtractor,
            final Function<? super Map.Entry<K, V>, ? extends VV> valueMapper) {
        return s.toMap(keyExtractor, valueMapper);
    }

    /**
     * 
     * @param mapFactory
     * @return
     */
    public <M extends Map<K, V>> M toMap(final Supplier<M> mapFactory) {
        final Function<? super Map.Entry<K, V>, K> classifier = Fn.key();
        final Function<? super Map.Entry<K, V>, V> valueMapper = Fn.value();

        return s.toMap(classifier, valueMapper, mapFactory);
    }

    /**
     * 
     * @param keyExtractor
     * @param valueMapper
     * @param mapFactory
     * @return
     * @see Collectors#toMap(Function, Function, Supplier)
     */
    public <KK, VV, M extends Map<KK, VV>> M toMap(final Function<? super Map.Entry<K, V>, ? extends KK> keyExtractor,
            final Function<? super Map.Entry<K, V>, ? extends VV> valueMapper, final Supplier<M> mapFactory) {
        return s.toMap(keyExtractor, valueMapper, mapFactory);
    }

    public Optional<Map.Entry<K, V>> reduce(final BinaryOperator<Map.Entry<K, V>> accumulator) {
        return s.reduce(accumulator);
    }

    public <U> U reduce(final U identity, final BiFunction<U, ? super Map.Entry<K, V>, U> accumulator) {
        return s.reduce(identity, accumulator);
    }

    public <R> R collect(final Supplier<R> supplier, final BiConsumer<R, ? super Map.Entry<K, V>> accumulator) {
        return s.collect(supplier, accumulator);
    }

    public <R, A> R collect(final Collector<? super Map.Entry<K, V>, A, R> collector) {
        return s.collect(collector);
    }

    public <K2, V2> EntryStream<K2, V2> chain(Function<? super Stream<Map.Entry<K, V>>, ? extends Stream<Map.Entry<K2, V2>>> transfer) {
        return of(transfer.apply(s));
    }

    public EntryStream<K, V> onClose(Runnable closeHandler) {
        return of(s.onClose(closeHandler));
    }

    public void close() {
        s.close();
    }
}
