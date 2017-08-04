package com.annimon.stream;

/**
 * A pair with int-valued first element and object-valued second element.
 *
 * @param <T> the type of the second element
 * @since 1.1.2
 */
public final class Indexed<T> {

    private final int index;
    private final T value;

    public Indexed(int index, T value) {
        this.index = index;
        this.value = value;
    }

    /**
     * index.
     *
     * @return index.
     */
    public int index() {
        return index;
    }

    /**
     * value.
     *
     * @return value.
     */
    public T value() {
        return value;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + this.index;
        hash = 97 * hash + (this.value != null ? this.value.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final Indexed<?> other = (Indexed<?>) obj;
        if (this.index != other.index)
            return false;
        return !(this.value != other.value && (this.value == null || !this.value.equals(other.value)));
    }

    @Override
    public String toString() {
        return "[" + index + "]=" + value;
    }
}
