package com.example.demo.metrics;

/**
 * Created by huoyijie on 18/11/30.
 */
@FunctionalInterface
public interface TargetAction<T> {
    T cost();
}
