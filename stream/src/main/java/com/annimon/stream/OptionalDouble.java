package com.annimon.stream;

import java.util.NoSuchElementException;

import com.landawn.abacus.util.function.DoubleConsumer;
import com.landawn.abacus.util.function.DoubleSupplier;
import com.landawn.abacus.util.function.Supplier;

/**
 * A container object which may or may not contain a {@code double} value.
 *
 * @since 1.1.4
 * @see Optional
 */
public final class OptionalDouble {

    private static final OptionalDouble EMPTY = new OptionalDouble();

    /**
     * Returns an empty {@code OptionalDouble} instance.
     *
     * @return an empty {@code OptionalDouble}
     */
    public static OptionalDouble empty() {
        return EMPTY;
    }

    /**
     * Returns an {@code OptionalDouble} with the specified value present.
     *
     * @param value  the value to be present
     * @return an {@code OptionalDouble} with the value present
     */
    public static OptionalDouble of(double value) {
        return new OptionalDouble(value);
    }

    private final boolean isPresent;
    private final double value;

    private OptionalDouble() {
        this.isPresent = false;
        this.value = 0;
    }

    private OptionalDouble(double value) {
        this.isPresent = true;
        this.value = value;
    }

    /**
     * Returns an inner value if present, otherwise throws {@code NoSuchElementException}.
     *
     * @return the inner value of this {@code OptionalDouble}
     * @throws NoSuchElementException if there is no value present
     * @see OptionalDouble#isPresent()
     */
    public double get() {
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
    public void ifPresent(DoubleConsumer consumer) {
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
    public void ifPresentOrElse(DoubleConsumer consumer, Runnable emptyAction) {
        if (isPresent) {
            consumer.accept(value);
        } else {
            emptyAction.run();
        }
    }

    /**
     * Wraps a value into {@code DoubleStream} if present,
     * otherwise returns an empty {@code DoubleStream}.
     *
     * @return the optional value as an {@code DoubleStream}
     */
    public DoubleStream stream() {
        if (!isPresent()) {
            return DoubleStream.empty();
        }
        return DoubleStream.of(value);
    }

    /**
     * Returns inner value if present, otherwise returns {@code other}.
     *
     * @param other  the value to be returned if there is no value present
     * @return the value, if present, otherwise {@code other}
     */
    public double orElse(double other) {
        return isPresent ? value : other;
    }

    /**
     * Returns the value if present, otherwise returns value produced by supplier function.
     *
     * @param other  supplier function that produces value if inner value is not present
     * @return the value if present otherwise the result of {@code other.getAsDouble()}
     * @throws NullPointerException if value is not present and {@code other} is null
     */
    public double orElseGet(DoubleSupplier other) {
        return isPresent ? value : other.getAsDouble();
    }

    /**
     * Returns the value if present, otherwise throws an exception provided by supplier function.
     *
     * @param <X> the type of exception to be thrown
     * @param exceptionSupplier  supplier function that produces an exception to be thrown
     * @return inner value if present
     * @throws X if inner value is not present
     */
    public <X extends Throwable> double orElseThrow(Supplier<X> exceptionSupplier) throws X {
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
        if (!(obj instanceof OptionalDouble)) {
            return false;
        }

        OptionalDouble other = (OptionalDouble) obj;
        return (isPresent && other.isPresent) ? Double.compare(value, other.value) == 0 : isPresent == other.isPresent;
    }

    @Override
    public int hashCode() {
        return isPresent ? Objects.hashCode(value) : 0;
    }

    @Override
    public String toString() {
        return isPresent ? String.format("OptionalDouble[%s]", value) : "OptionalDouble.empty";
    }
}
