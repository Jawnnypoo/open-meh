package com.jawnnypoo.openmeh.util;

/**
 * Simple callback
 */
public interface Callback<T> {
    /**
     * Something good happened
     */
    void onSuccess(T response);

    /**
     * Something bad happened
     */
    void onFailure(Throwable t);
}