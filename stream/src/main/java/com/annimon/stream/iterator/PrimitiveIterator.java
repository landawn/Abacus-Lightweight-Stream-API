package com.annimon.stream.iterator;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A base type for primitive specializations of {@link Iterator}.
 * Specialized subtypes are provided for {@link OfInt int},
 * {@link OfLong long} and {@link OfDouble double} values.
 */
public final class PrimitiveIterator {

    private PrimitiveIterator() {
    }

    public abstract static class OfInt implements Iterator<Integer> {

        public static final OfInt EMPTY = new OfInt() {
            @Override
            public boolean hasNext() {
                return false;
            }

            @Override
            public int nextInt() {
                throw new NoSuchElementException();
            }
        };

        public static OfInt of(final int[] a) {
            if (a == null || a.length == 0) {
                return EMPTY;
            }

            return new OfInt() {
                private final int[] aar = a;
                private final int len = aar.length;
                private int cursor = 0;

                @Override
                public boolean hasNext() {
                    return cursor < len;
                }

                @Override
                public int nextInt() {
                    if (cursor >= len) {
                        throw new NoSuchElementException();
                    }

                    return aar[cursor++];
                }
            };
        }

        public abstract int nextInt();

        @Override
        public Integer next() {
            return nextInt();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("remove");
        }
    }

    public abstract static class OfLong implements Iterator<Long> {

        public static final OfLong EMPTY = new OfLong() {
            @Override
            public boolean hasNext() {
                return false;
            }

            @Override
            public long nextLong() {
                throw new NoSuchElementException();
            }
        };

        public static OfLong of(final long[] a) {
            if (a == null || a.length == 0) {
                return EMPTY;
            }

            return new OfLong() {
                private final long[] aar = a;
                private final int len = aar.length;
                private int cursor = 0;

                @Override
                public boolean hasNext() {
                    return cursor < len;
                }

                @Override
                public long nextLong() {
                    if (cursor >= len) {
                        throw new NoSuchElementException();
                    }

                    return aar[cursor++];
                }
            };
        }

        public abstract long nextLong();

        @Override
        public Long next() {
            return nextLong();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("remove");
        }
    }

    public abstract static class OfDouble implements Iterator<Double> {

        public static final OfDouble EMPTY = new OfDouble() {
            @Override
            public boolean hasNext() {
                return false;
            }

            @Override
            public double nextDouble() {
                throw new NoSuchElementException();
            }
        };

        public static OfDouble of(final double[] a) {
            if (a == null || a.length == 0) {
                return EMPTY;
            }

            return new OfDouble() {
                private final double[] aar = a;
                private final int len = aar.length;
                private int cursor = 0;

                @Override
                public boolean hasNext() {
                    return cursor < len;
                }

                @Override
                public double nextDouble() {
                    if (cursor >= len) {
                        throw new NoSuchElementException();
                    }

                    return aar[cursor++];
                }
            };
        }

        public abstract double nextDouble();

        @Override
        public Double next() {
            return nextDouble();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("remove");
        }
    }
}