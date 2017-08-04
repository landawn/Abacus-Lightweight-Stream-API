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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.landawn.abacus.util.Fn;
import com.landawn.abacus.util.function.BiConsumer;
import com.landawn.abacus.util.function.BinaryOperator;
import com.landawn.abacus.util.function.Consumer;
import com.landawn.abacus.util.function.Function;
import com.landawn.abacus.util.function.Predicate;
import com.landawn.abacus.util.function.Supplier;
import com.landawn.abacus.util.function.ToDoubleFunction;
import com.landawn.abacus.util.function.ToIntFunction;
import com.landawn.abacus.util.function.ToLongFunction;

/**
 * Common implementations of {@code Collector} interface.
 * 
 * @see Collector
 */
public final class Collectors {

    private static final Supplier<long[]> LONG_2ELEMENTS_ARRAY_SUPPLIER = new Supplier<long[]>() {
        @Override
        public long[] get() {
            return new long[] { 0L, 0L };
        }
    };

    private static final Supplier<double[]> DOUBLE_2ELEMENTS_ARRAY_SUPPLIER = new Supplier<double[]>() {
        @Override
        public double[] get() {
            return new double[] { 0d, 0d };
        }
    };

    private Collectors() {
    }

    /**
     * Returns a {@code Collector} that fills new {@code Collection}, provided by {@code collectionSupplier},
     * with input elements.
     * 
     * @param <T> the type of the input elements
     * @param <R> the type of the resulting collection
     * @param collectionSupplier  a supplier function that provides new collection
     * @return a {@code Collector}
     */
    public static <T, R extends Collection<T>> Collector<T, ?, R> toCollection(Supplier<R> collectionSupplier) {
        return new CollectorsImpl<>(

                collectionSupplier,

                new BiConsumer<R, T>() {
                    @Override
                    public void accept(R t, T u) {
                        t.add(u);
                    }
                });
    }

    /**
     * Returns a {@code Collector} that fills new {@code List} with input elements.
     * 
     * @param <T> the type of the input elements
     * @return a {@code Collector}
     */
    public static <T> Collector<T, ?, List<T>> toList() {
        return new CollectorsImpl<>(

                new Supplier<List<T>>() {
                    @Override
                    public List<T> get() {
                        return new ArrayList<>();
                    }
                },

                new BiConsumer<List<T>, T>() {
                    @Override
                    public void accept(List<T> t, T u) {
                        t.add(u);
                    }
                });
    }

    /**
     * Returns a {@code Collector} that fills new {@code Set} with input elements.
     * 
     * @param <T> the type of the input elements
     * @return a {@code Collector}
     */
    public static <T> Collector<T, ?, Set<T>> toSet() {
        return new CollectorsImpl<>(

                new Supplier<Set<T>>() {
                    @Override
                    public Set<T> get() {
                        return new HashSet<>();
                    }
                },

                new BiConsumer<Set<T>, T>() {
                    @Override
                    public void accept(Set<T> t, T u) {
                        t.add(u);
                    }
                });
    }

    /**
     * Returns a {@code Collector} that fills new {@code Map} with input elements.
     *
     * @param <T> the type of the input elements and the result type of value mapping function
     * @param <K> the result type of key mapping function
     * @param keyMapper  a mapping function to produce keys
     * @return a {@code Collector}
     * @since 1.1.3
     */
    public static <K, T> Collector<T, ?, Map<K, T>> toMap(final Function<? super T, ? extends K> keyMapper) {
        return Collectors.<T, K, T> toMap(keyMapper, Fn.<T> identity());
    }

    /**
     * Returns a {@code Collector} that fills new {@code Map} with input elements.
     *
     * @param <T> the type of the input elements
     * @param <K> the result type of key mapping function
     * @param <V> the result type of value mapping function
     * @param keyMapper  a mapping function to produce keys
     * @param valueMapper  a mapping function to produce values
     * @return a {@code Collector}
     */
    public static <T, K, V> Collector<T, ?, Map<K, V>> toMap(final Function<? super T, ? extends K> keyMapper,
            final Function<? super T, ? extends V> valueMapper) {
        return Collectors.<T, K, V, Map<K, V>> toMap(keyMapper, valueMapper, Collectors.<K, V> hashMapSupplier());
    }

    /**
     * Returns a {@code Collector} that fills new {@code Map} with input elements.
     * 
     * @param <T> the type of the input elements
     * @param <K> the result type of key mapping function
     * @param <V> the result type of value mapping function
     * @param <M> the type of the resulting {@code Map}
     * @param keyMapper  a mapping function to produce keys
     * @param valueMapper  a mapping function to produce values
     * @param mapFactory  a supplier function that provides new {@code Map}
     * @return a {@code Collector}
     */
    public static <T, K, V, M extends Map<K, V>> Collector<T, ?, M> toMap(final Function<? super T, ? extends K> keyMapper,
            final Function<? super T, ? extends V> valueMapper, final Supplier<M> mapFactory) {
        return new CollectorsImpl<>(

                mapFactory,

                new BiConsumer<M, T>() {
                    @Override
                    public void accept(M map, T t) {
                        final K key = keyMapper.apply(t);
                        final V value = valueMapper.apply(t);
                        final V oldValue = map.get(key);
                        final V newValue = (oldValue == null) ? value : oldValue;
                        if (newValue == null) {
                            map.remove(key);
                        } else {
                            map.put(key, newValue);
                        }
                    }
                });
    }

    /**
     * Returns a {@code Collector} that concatenates input elements into new string.
     * 
     * @return a {@code Collector}
     */
    public static Collector<CharSequence, ?, String> joining() {
        return joining("");
    }

    /**
     * Returns a {@code Collector} that concatenates input elements into new string.
     * 
     * @param delimiter  the delimiter between each element
     * @return a {@code Collector}
     */
    public static Collector<CharSequence, ?, String> joining(CharSequence delimiter) {
        return joining(delimiter, "", "");
    }

    /**
     * Returns a {@code Collector} that concatenates input elements into new string.
     * 
     * @param delimiter  the delimiter between each element
     * @param prefix  the prefix of result
     * @param suffix  the suffix of result
     * @return a {@code Collector}
     */
    public static Collector<CharSequence, ?, String> joining(CharSequence delimiter, CharSequence prefix, CharSequence suffix) {
        return joining(delimiter, prefix, suffix, prefix.toString() + suffix.toString());
    }

    /**
     * Returns a {@code Collector} that concatenates input elements into new string.
     * 
     * @param delimiter  the delimiter between each element
     * @param prefix  the prefix of result
     * @param suffix  the suffix of result
     * @param emptyValue  the string which replaces empty element if exists
     * @return a {@code Collector}
     */
    public static Collector<CharSequence, ?, String> joining(final CharSequence delimiter, final CharSequence prefix, final CharSequence suffix,
            final String emptyValue) {
        return new CollectorsImpl<>(

                new Supplier<StringBuilder>() {
                    @Override
                    public StringBuilder get() {
                        return new StringBuilder();
                    }
                },

                new BiConsumer<StringBuilder, CharSequence>() {
                    @Override
                    public void accept(StringBuilder t, CharSequence u) {
                        if (t.length() > 0) {
                            t.append(delimiter);
                        } else {
                            t.append(prefix);
                        }
                        t.append(u);
                    }
                },

                new Function<StringBuilder, String>() {
                    @Override
                    public String apply(StringBuilder value) {
                        if (value.length() == 0) {
                            return emptyValue;
                        } else {
                            value.append(suffix);
                            return value.toString();
                        }
                    }
                });
    }

    /**
     * Returns a {@code Collector} that calculates average of integer-valued input elements.
     *
     * @param <T> the type of the input elements
     * @param mapper  the mapping function which extracts value from element to calculate result
     * @return a {@code Collector}
     * @since 1.1.3
     */
    public static <T> Collector<T, ?, Double> averagingInt(final ToIntFunction<? super T> mapper) {
        return averagingHelper(new BiConsumer<long[], T>() {
            @Override
            public void accept(long[] t, T u) {
                t[0]++; // count
                t[1] += mapper.applyAsInt(u); // sum
            }
        });
    }

    /**
     * Returns a {@code Collector} that calculates average of long-valued input elements.
     *
     * @param <T> the type of the input elements
     * @param mapper  the mapping function which extracts value from element to calculate result
     * @return a {@code Collector}
     * @since 1.1.3
     */
    public static <T> Collector<T, ?, Double> averagingLong(final ToLongFunction<? super T> mapper) {
        return averagingHelper(new BiConsumer<long[], T>() {
            @Override
            public void accept(long[] t, T u) {
                t[0]++; // count
                t[1] += mapper.applyAsLong(u); // sum
            }
        });
    }

    private static <T> Collector<T, ?, Double> averagingHelper(final BiConsumer<long[], T> accumulator) {
        return new CollectorsImpl<>(

                LONG_2ELEMENTS_ARRAY_SUPPLIER,

                accumulator,

                new Function<long[], Double>() {
                    @Override
                    public Double apply(long[] t) {
                        if (t[0] == 0)
                            return 0d;
                        return t[1] / (double) t[0];
                    }
                });
    }

    /**
     * Returns a {@code Collector} that calculates average of double-valued input elements.
     *
     * @param <T> the type of the input elements
     * @param mapper  the mapping function which extracts value from element to calculate result
     * @return a {@code Collector}
     * @since 1.1.3
     */
    public static <T> Collector<T, ?, Double> averagingDouble(final ToDoubleFunction<? super T> mapper) {
        return new CollectorsImpl<>(

                DOUBLE_2ELEMENTS_ARRAY_SUPPLIER,

                new BiConsumer<double[], T>() {
                    @Override
                    public void accept(double[] t, T u) {
                        t[0]++; // count
                        t[1] += mapper.applyAsDouble(u); // sum
                    }
                },

                new Function<double[], Double>() {
                    @Override
                    public Double apply(double[] t) {
                        if (t[0] == 0)
                            return 0d;
                        return t[1] / t[0];
                    }
                });
    }

    /**
     * Returns a {@code Collector} that summing integer-valued input elements.
     *
     * @param <T> the type of the input elements
     * @param mapper  the mapping function which extracts value from element to calculate result
     * @return a {@code Collector}
     * @since 1.1.3
     */
    public static <T> Collector<T, ?, Integer> summingInt(final ToIntFunction<? super T> mapper) {
        return new CollectorsImpl<>(

                new Supplier<int[]>() {
                    @Override
                    public int[] get() {
                        return new int[] { 0 };
                    }
                },

                new BiConsumer<int[], T>() {
                    @Override
                    public void accept(int[] t, T u) {
                        t[0] += mapper.applyAsInt(u);
                    }
                },

                new Function<int[], Integer>() {
                    @Override
                    public Integer apply(int[] value) {
                        return value[0];
                    }
                });
    }

    /**
     * Returns a {@code Collector} that summing long-valued input elements.
     *
     * @param <T> the type of the input elements
     * @param mapper  the mapping function which extracts value from element to calculate result
     * @return a {@code Collector}
     * @since 1.1.3
     */
    public static <T> Collector<T, ?, Long> summingLong(final ToLongFunction<? super T> mapper) {
        return new CollectorsImpl<>(

                LONG_2ELEMENTS_ARRAY_SUPPLIER,

                new BiConsumer<long[], T>() {
                    @Override
                    public void accept(long[] t, T u) {
                        t[0] += mapper.applyAsLong(u);
                    }
                },

                new Function<long[], Long>() {
                    @Override
                    public Long apply(long[] value) {
                        return value[0];
                    }
                });
    }

    /**
     * Returns a {@code Collector} that summing double-valued input elements.
     *
     * @param <T> the type of the input elements
     * @param mapper  the mapping function which extracts value from element to calculate result
     * @return a {@code Collector}
     * @since 1.1.3
     */
    public static <T> Collector<T, ?, Double> summingDouble(final ToDoubleFunction<? super T> mapper) {
        return new CollectorsImpl<>(

                DOUBLE_2ELEMENTS_ARRAY_SUPPLIER,

                new BiConsumer<double[], T>() {
                    @Override
                    public void accept(double[] t, T u) {
                        t[0] += mapper.applyAsDouble(u);
                    }
                },

                new Function<double[], Double>() {
                    @Override
                    public Double apply(double[] value) {
                        return value[0];
                    }
                });
    }

    /**
     * Returns a {@code Collector} that counts the number of input elements.
     * 
     * @param <T> the type of the input elements
     * @return a {@code Collector}
     */
    public static <T> Collector<T, ?, Integer> counting() {
        return summingInt(new ToIntFunction<T>() {

            @Override
            public int applyAsInt(T t) {
                return 1;
            }
        });
    }

    /**
     * Returns a {@code Collector} that reduces input elements.
     * 
     * @param <T> the type of the input elements
     * @param identity  the initial value
     * @param op  the operator to reduce elements
     * @return a {@code Collector}
     * @see #reducing(java.lang.Object, com.landawn.abacus.util.function.Function, com.landawn.abacus.util.function.BinaryOperator) 
     */
    public static <T> Collector<T, ?, T> reducing(final T identity, final BinaryOperator<T> op) {
        return new CollectorsImpl<>(

                new Supplier<Tuple1<T>>() {
                    @Override
                    public Tuple1<T> get() {
                        return new Tuple1<>(identity);
                    }
                },

                new BiConsumer<Tuple1<T>, T>() {
                    @Override
                    public void accept(Tuple1<T> tuple, T value) {
                        tuple.a = op.apply(tuple.a, value);
                    }
                },

                new Function<Tuple1<T>, T>() {
                    @Override
                    public T apply(Tuple1<T> tuple) {
                        return tuple.a;
                    }
                });
    }

    /**
     * Returns a {@code Collector} that reduces input elements.
     * 
     * @param <T> the type of the input elements
     * @param <R> the type of the output elements
     * @param identity  the initial value
     * @param mapper  the mapping function
     * @param op  the operator to reduce elements
     * @return a {@code Collector}
     * @see #reducing(java.lang.Object, com.landawn.abacus.util.function.BinaryOperator) 
     */
    public static <T, R> Collector<T, ?, R> reducing(final R identity, final Function<? super T, ? extends R> mapper, final BinaryOperator<R> op) {
        return new CollectorsImpl<>(

                new Supplier<Tuple1<R>>() {
                    @Override
                    public Tuple1<R> get() {
                        return new Tuple1<>(identity);
                    }
                },

                new BiConsumer<Tuple1<R>, T>() {
                    @Override
                    public void accept(Tuple1<R> tuple, T value) {
                        tuple.a = op.apply(tuple.a, mapper.apply(value));
                    }
                },

                new Function<Tuple1<R>, R>() {
                    @Override
                    public R apply(Tuple1<R> tuple) {
                        return tuple.a;
                    }
                });
    }

    /**
     * Returns a {@code Collector} that filters input elements.
     *
     * @param <T> the type of the input elements
     * @param <A> the accumulation type
     * @param <R> the type of the output elements
     * @param predicate  a predicate used to filter elements
     * @param downstream  the collector of filtered elements
     * @return a {@code Collector}
     * @since 1.1.3
     */
    public static <T, A, R> Collector<T, ?, R> filtering(final Predicate<? super T> predicate, final Collector<? super T, A, R> downstream) {
        final BiConsumer<A, ? super T> accumulator = downstream.accumulator();
        return new CollectorsImpl<>(

                downstream.supplier(),

                new BiConsumer<A, T>() {
                    @Override
                    public void accept(A a, T t) {
                        if (predicate.test(t))
                            accumulator.accept(a, t);
                    }
                },

                downstream.finisher());
    }

    /**
     * Returns a {@code Collector} that performs mapping before accumulation.
     * 
     * @param <T> the type of the input elements
     * @param <U> the result type of mapping function
     * @param <A> the accumulation type
     * @param <R> the result type of collector
     * @param mapper  a function that performs mapping to input elements
     * @param downstream  the collector of mapped elements
     * @return a {@code Collector}
     */
    public static <T, U, A, R> Collector<T, ?, R> mapping(final Function<? super T, ? extends U> mapper, final Collector<? super U, A, R> downstream) {

        final BiConsumer<A, ? super U> accumulator = downstream.accumulator();
        return new CollectorsImpl<>(

                downstream.supplier(),

                new BiConsumer<A, T>() {
                    @Override
                    public void accept(A a, T t) {
                        accumulator.accept(a, mapper.apply(t));
                    }
                },

                downstream.finisher());
    }

    /**
     * Returns a {@code Collector} that performs flat-mapping before accumulation.
     *
     * @param <T> the type of the input elements
     * @param <U> the result type of flat-mapping function
     * @param <A> the accumulation type
     * @param <R> the result type of collector
     * @param mapper  a function that performs flat-mapping to input elements
     * @param downstream  the collector of flat-mapped elements
     * @return a {@code Collector}
     * @since 1.1.3
     */
    public static <T, U, A, R> Collector<T, ?, R> flatMapping(final Function<? super T, ? extends Stream<? extends U>> mapper,
            final Collector<? super U, A, R> downstream) {

        final BiConsumer<A, ? super U> accumulator = downstream.accumulator();
        return new CollectorsImpl<>(

                downstream.supplier(),

                new BiConsumer<A, T>() {
                    @Override
                    public void accept(final A a, T t) {
                        final Stream<? extends U> stream = mapper.apply(t);
                        if (stream == null)
                            return;
                        stream.forEach(new Consumer<U>() {
                            @Override
                            public void accept(U u) {
                                accumulator.accept(a, u);
                            }
                        });
                    }
                },

                downstream.finisher());
    }

    /**
     * Returns a {@code Collector} that performs additional transformation.
     * 
     * @param <T> the type of the input elements
     * @param <A> the accumulation type
     * @param <IR> the input type of the transformation function
     * @param <OR> the output type of the transformation function
     * @param c  the input {@code Collector}
     * @param finisher  the final transformation function
     * @return a {@code Collector}
     */
    public static <T, A, IR, OR> Collector<T, A, OR> collectingAndThen(final Collector<T, A, IR> c, final Function<IR, OR> finisher) {
        @SuppressWarnings({ "rawtypes", "unchecked" })
        final Function<A, IR> downstreamFinisher = c.finisher() == null ? (Function) castIdentity() : c.finisher();

        final Function<A, OR> newFinisher = new Function<A, OR>() {
            @Override
            public OR apply(A t) {
                return finisher.apply(downstreamFinisher.apply(t));
            }
        };

        return new CollectorsImpl<>(c.supplier(), c.accumulator(), newFinisher);
    }

    /**
     * Returns a {@code Collector} that performs grouping operation by given classifier.
     * 
     * @param <T> the type of the input elements
     * @param <K> the type of the keys
     * @param classifier  the classifier function 
     * @return a {@code Collector}
     * @see #groupingBy(com.landawn.abacus.util.function.Function, com.annimon.stream.Collector) 
     * @see #groupingBy(com.landawn.abacus.util.function.Function, com.annimon.stream.Collector, com.landawn.abacus.util.function.Supplier) 
     */
    public static <T, K> Collector<T, ?, Map<K, List<T>>> groupingBy(Function<? super T, ? extends K> classifier) {
        return groupingBy(classifier, Collectors.<T> toList());
    }

    /**
     * Returns a {@code Collector} that performs grouping operation by given classifier.
     * 
     * @param <T> the type of the input elements
     * @param <K> the type of the keys
     * @param <A> the accumulation type
     * @param <D> the result type of downstream reduction
     * @param classifier  the classifier function 
     * @param downstream  the collector of mapped elements
     * @return a {@code Collector}
     * @see #groupingBy(com.landawn.abacus.util.function.Function) 
     * @see #groupingBy(com.landawn.abacus.util.function.Function, com.annimon.stream.Collector, com.landawn.abacus.util.function.Supplier) 
     */
    public static <T, K, A, D> Collector<T, ?, Map<K, D>> groupingBy(Function<? super T, ? extends K> classifier, Collector<? super T, A, D> downstream) {
        return Collectors.<T, K, D, A, Map<K, D>> groupingBy(classifier, downstream, Collectors.<K, D> hashMapSupplier());
    }

    /**
     * Returns a {@code Collector} that performs grouping operation by given classifier.
     * 
     * @param <T> the type of the input elements
     * @param <K> the type of the keys
     * @param <A> the accumulation type
     * @param <D> the result type of downstream reduction
     * @param <M> the type of the resulting {@code Map}
     * @param classifier  the classifier function 
     * @param downstream  the collector of mapped elements
     * @param mapFactory  a supplier function that provides new {@code Map}
     * @return a {@code Collector}
     * @see #groupingBy(com.landawn.abacus.util.function.Function) 
     * @see #groupingBy(com.landawn.abacus.util.function.Function, com.annimon.stream.Collector) 
     */
    public static <T, K, D, A, M extends Map<K, D>> Collector<T, ?, M> groupingBy(final Function<? super T, ? extends K> classifier,
            final Collector<? super T, A, D> downstream, final Supplier<M> mapFactory) {

        @SuppressWarnings("unchecked")
        final Function<A, A> downstreamFinisher = (Function<A, A>) downstream.finisher();
        Function<Map<K, A>, M> finisher = null;
        if (downstreamFinisher != null) {
            finisher = new Function<Map<K, A>, M>() {
                @Override
                public M apply(Map<K, A> map) {
                    // Update values of a map by a finisher function
                    for (Map.Entry<K, A> entry : map.entrySet()) {
                        A value = entry.getValue();
                        value = downstreamFinisher.apply(value);
                        entry.setValue(value);
                    }
                    @SuppressWarnings("unchecked")
                    M castedMap = (M) map;
                    return castedMap;
                }
            };
        }

        @SuppressWarnings("unchecked")
        Supplier<Map<K, A>> castedMapFactory = (Supplier<Map<K, A>>) mapFactory;
        return new CollectorsImpl<>(castedMapFactory,

                new BiConsumer<Map<K, A>, T>() {
                    @Override
                    public void accept(Map<K, A> map, T t) {
                        K key = Objects.requireNonNull(classifier.apply(t), "element cannot be mapped to a null key");
                        // Get container with currently grouped elements
                        A container = map.get(key);
                        if (container == null) {
                            // Put new container (list, map, set, etc)
                            container = downstream.supplier().get();
                            map.put(key, container);
                        }
                        // Add element to container
                        downstream.accumulator().accept(container, t);
                    }
                },

                finisher);
    }

    private static <K, V> Supplier<Map<K, V>> hashMapSupplier() {
        return new Supplier<Map<K, V>>() {

            @Override
            public Map<K, V> get() {
                return new HashMap<>();
            }
        };
    }

    @SuppressWarnings("unchecked")
    static <A, R> Function<A, R> castIdentity() {
        return new Function<A, R>() {

            @Override
            public R apply(A value) {
                return (R) value;
            }
        };
    }

    private static final class Tuple1<A> {
        A a;

        Tuple1(A a) {
            this.a = a;
        }
    }

    private static final class CollectorsImpl<T, A, R> implements Collector<T, A, R> {

        private final Supplier<A> supplier;
        private final BiConsumer<A, T> accumulator;
        private final Function<A, R> finisher;

        public CollectorsImpl(Supplier<A> supplier, BiConsumer<A, T> accumulator) {
            this(supplier, accumulator, null);
        }

        public CollectorsImpl(Supplier<A> supplier, BiConsumer<A, T> accumulator, Function<A, R> finisher) {
            this.supplier = supplier;
            this.accumulator = accumulator;
            this.finisher = finisher;
        }

        @Override
        public Supplier<A> supplier() {
            return supplier;
        }

        @Override
        public BiConsumer<A, T> accumulator() {
            return accumulator;
        }

        @Override
        public Function<A, R> finisher() {
            return finisher;
        }

    }
}
