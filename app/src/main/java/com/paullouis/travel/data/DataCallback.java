package com.paullouis.travel.data;

/**
 * A generic callback interface for asynchronous data operations.
 *
 * @param <T> The type of the expected result on success.
 */
public interface DataCallback<T> {
    void onSuccess(T result);
    void onError(Exception e);
}
