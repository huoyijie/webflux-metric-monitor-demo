package com.example.demo.metrics;


import java.util.Map;

import static java.lang.System.out;

/**
 * Created by huoyijie on 18/11/30.
 */
public class Metric {
    public static <T> T latency(String metricName, String targetName, TargetAction<T> action, Map<String, String> tags) {
        long timestamp = System.currentTimeMillis();
        long start = System.nanoTime();
        StringBuilder __tags__ = new StringBuilder("");
        tags.forEach((tagName, tagValue) -> __tags__.append(String.format("%s=%s,", tagName, tagValue)));
        String status = "success";
        T result = null;
        try {
            result = action.cost();
        } catch (Throwable t) {
            status = "fail";
            out.println(t.getMessage());
        } finally {
            float cost = (System.nanoTime() - start) / 1000000f;
            MetricContainer.queue.add(new Latency(timestamp, metricName, targetName, status, tags, cost));
            out.println(String.format(
                    "%d %s %s %s [%s] cost %fms", timestamp,
                    metricName, targetName, status, __tags__, cost));
        }
        return result;
    }

}