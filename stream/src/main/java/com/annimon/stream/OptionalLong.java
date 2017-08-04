package com.annimon.stream;

import java.util.NoSuchElementException;

import com.annimon.stream.function.LongConsumer;
import com.annimon.stream.function.LongSupplier;
import com.annimon.stream.function.Supplier;

/**
 * A container object which may or may not contain a {@code long} value.
 *
 * @since 1.1.4
 * @see Optional
 */
public final class OptionalLong {

    private static final OptionalLong EMPTY = new OptionalLong();

    /**
     * Returns an empty {@code OptionalLong} instance.
     *
     * @return an empty {@code OptionalLong}
     */
    public static OptionalLong empty() {
        return EMPTY;
    }

    /**
     * Returns an {@code OptionalLong} with the specified value present.
     *
     * @param value  the value to be present
     * @return an {@code OptionalLong} with the value present
     */
    public static OptionalLong of(long value) {
        return new OptionalLong(value);
    }

    private final boolean isPresent;
    private final long value;

    private OptionalLong() {
        this.isPresent = false;
        this.value = 0;
    }

    private OptionalLong(long value) {
        this.isPresent = true;
        this.value = value;
    }

    /**
     * Returns an inner value if present, otherwise throws {@code NoSuchElementException}.
     *
     * @return the inner value of this {@code OptionalLong}
     * @throws NoSuchElementException if there is no value present
     * @see OptionalLong#isPresent()
     */
    public long getAsLong() {
        if (!isPresent) {
            throw new NoSuchElementException("No value present");
        }
        return value;
    }

    /**
     * Checks value present.
     *
     * @return {@code true} if a value present, {@code false} otherwise
     */
    public boolean isPresent() {
        return isPresent;
    }

    /**
     * Invokes consumer function with value if present, otherwise does nothing.
     *
     * @param consumer  the consumer function to be executed if a value is present
     * @throws NullPointerException if value is present and {@code consumer} is null
     */
    public void ifPresent(LongConsumer consumer) {
        if (isPresent) {
            consumer.accept(value);
        }
    }

    /**
     * If a value is present, performs the given action with the value,
     * otherwise performs the empty-based action.
     *
     * @param consumer  the consumer function to be executed, if a value is present
     * @param emptyAction  the empty-based action to be performed, if no value is present
     * @throws NullPointerException if a value is present and the given consumer function is null,
     *         or no value is present and the given empty-based action is null.
     */
    public void ifPresentOrElse(LongConsumer consumer, Runnable emptyAction) {
        if (isPresent) {
            consumer.accept(value);
        } else {
            emptyAction.run();
        }
    }

    /**
     * Wraps a value into {@code LongStream} if present,
     * otherwise returns an empty {@code LongStream}.
     *
     * @return the optional value as an {@code LongStream}
     */
    public LongStream stream() {
        if (!isPresent()) {
            return LongStream.empty();
        }
        return LongStream.of(value);
    }

    /**
     * Returns inner value if present, otherwise returns {@code other}.
     *
     * @param other  the value to be returned if there is no value present
     * @return the value, if present, otherwise {@code other}
     */
    public long orElse(long other) {
        return isPresent ? value : other;
    }

    /**
     * Returns the value if present, otherwise returns value produced by supplier function.
     *
     * @param other  supplier function that produces value if inner value is not present
     * @return the value if present otherwise the result of {@code other.getAsLong()}
     * @throws NullPointerException if value is not present and {@code other} is null
     */
    public long orElseGet(LongSupplier other) {
        return isPresent ? value : other.getAsLong();
    }

    /**
     * Returns the value if present, otherwise throws an exception provided by supplier function.
     *
     * @param <X> the type of exception to be thrown
     * @param exceptionSupplier  supplier function that produces an exception to be thrown
     * @return inner value if present
     * @throws X if inner value is not present
     */
    public <X extends Throwable> long orElseThrow(Supplier<X> exceptionSupplier) throws X {
        if (isPresent) {
            return value;
        } else {
            throw exceptionSupplier.get();
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof OptionalLong)) {
            return false;
        }

        OptionalLong other = (OptionalLong) obj;
        return (isPresent && other.isPresent) ? value == other.value : isPresent == other.isPresent;
    }

    @Override
    public int hashCode() {
        return isPresent ? Objects.hashCode(value) : 0;
    }

    @Override
    public String toString() {
        return isPresent ? String.format("OptionalLong[%s]", value) : "OptionalLong.empty";
    }
}
