package org.bigbrainmm.avitoadminapi.dto;

/**
 * The type Tuple.
 *
 * @param <T1> the type parameter
 * @param <T2> the type parameter
 */
public class Tuple<T1, T2> {
    private final T1 first;
    private final T2 second;

    /**
     * Instantiates a new Tuple.
     *
     * @param first  the first
     * @param second the second
     */
    public Tuple(T1 first, T2 second) {
        this.first = first;
        this.second = second;
    }

    /**
     * Gets first.
     *
     * @return the first
     */
    public T1 getFirst() {
        return first;
    }

    /**
     * Gets second.
     *
     * @return the second
     */
    public T2 getSecond() {
        return second;
    }
}