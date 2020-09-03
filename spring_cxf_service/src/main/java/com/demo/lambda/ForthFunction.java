package com.demo.lambda;

@FunctionalInterface
public interface ForthFunction<T, U, K, E, R> {
    R apply(T t, U u, K k, E e);
}
