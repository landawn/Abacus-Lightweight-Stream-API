package com.annimon.stream;

import java.util.NoSuchElementException;

import com.annimon.stream.function.BooleanConsumer;
import com.annimon.stream.function.BooleanSupplier;
import com.annimon.stream.function.Supplier;

/**
 * A container object which may or may not contain a {@code boolean} value.
 *
 * @since 1.1.8
 * @see Optional
 */
public final class OptionalBoolean {

    private static final OptionalBoolean EMPTY = new OptionalBoolean();
    private static final OptionalBoolean TRUE = new OptionalBoolean(true);
    private static final OptionalBoolean FALSE = new OptionalBoolean(false);

    /**
     * Returns an empty {@code OptionalBoolean} instance.
     *
     * @return an empty {@code OptionalBoolean}
     */
    public static OptionalBoolean empty() {
        return EMPTY;
    }

    /**
     * Returns an {@code OptionalBoolean} with the specified value present.
     *
     * @param value  the value to be present
     * @return an {@code OptionalBoolean} with the value present
     */
    public static OptionalBoolean of(boolean value) {
        return value ? TRUE : FALSE;
    }

    private final boolean isPresent;
    private final boolean value;

    private OptionalBoolean() {
        this.isPresent = false;
        this.value = false;
    }

    private OptionalBoolean(boolean value) {
        this.isPresent = true;
        this.value = value;
    }

    /**
     * Returns an inner value if present, otherwise throws {@code NoSuchElementException}.
     *
     * @return the inner value of this {@code OptionalBoolean}
     * @throws NoSuchElementException if there is no value present
     * @see OptionalBoolean#isPresent()
     */
    public boolean get() {
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
    public void ifPresent(BooleanConsumer consumer) {
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
    public void ifPresentOrElse(BooleanConsumer consumer, Runnable emptyAction) {
        if (isPresent) {
            consumer.accept(value);
        } else {
            emptyAction.run();
        }
    }

    /**
     * Returns inner value if present, otherwise returns {@code other}.
     *
     * @param other  the value to be returned if there is no value present
     * @return the value, if present, otherwise {@code other}
     */
    public boolean orElse(boolean other) {
        return isPresent ? value : other;
    }

    /**
     * Returns the value if present, otherwise returns value produced by supplier function.
     *
     * @param other  supplier function that produces value if inner value is not present
     * @return the value if present otherwise the result of {@code other.getAsBoolean()}
     * @throws NullPointerException if value is not present and {@code other} is null
     */
    public boolean orElseGet(BooleanSupplier other) {
        return isPresent ? value : other.getAsBoolean();
    }

    /**
     * Returns the value if present, otherwise throws an exception provided by supplier function.
     *
     * @param <X> the type of exception to be thrown
     * @param exceptionSupplier  supplier function that produces an exception to be thrown
     * @return inner value if present
     * @throws X if inner value is not present
     */
    public <X extends Throwable> boolean orElseThrow(Supplier<X> exceptionSupplier) throws X {
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
        if (!(obj instanceof OptionalBoolean)) {
            return false;
        }

        OptionalBoolean other = (OptionalBoolean) obj;
        return (isPresent && other.isPresent) ? value == other.value : isPresent == other.isPresent;
    }

    @Override
    public int hashCode() {
        return isPresent ? (value ? 1231 : 1237) : 0;
    }

    @Override
    public String toString() {
        return isPresent ? (value ? "OptionalBoolean[true]" : "OptionalBoolean[false]") : "OptionalBoolean.empty";
    }
}
