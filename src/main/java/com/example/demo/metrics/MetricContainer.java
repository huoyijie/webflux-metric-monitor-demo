package com.example.demo.metrics;

import org.apache.commons.math3.stat.descriptive.rank.Percentile;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

/**
 * Created by huoyijie on 18/11/30.
 */
public class MetricContainer {
    public static final ConcurrentLinkedQueue<Latency> queue = new ConcurrentLinkedQueue<>();

    private static Statics statics(long since, List<Latency> latencyList) {
        if (latencyList.size() > 0) {
            int totalCount = latencyList.size();
            double avgCost = latencyList.stream().mapToDouble(Latency::getCost).sum() / totalCount;
            float qps = calcQps(latencyList);

            double[] costArray = latencyList.stream().mapToDouble(Latency::getCost).toArray();
            Percentile percentile = new Percentile();
            double pct90Cost = percentile.evaluate(costArray, 90);
            double pct99Cost = percentile.evaluate(costArray, 99);

            float successRate = latencyList.stream().mapToInt(latency -> latency.succeed() ? 1 : 0).sum() / (float) totalCount;

            return new Statics(totalCount, qps, avgCost, pct90Cost, pct99Cost, successRate, new Date(since).toString());
        } else {
            return new Statics(0, 0, 0, 0, 0, 0, new Date(since).toString());
        }
    }

    public static Statics staticsBySecond() {
        long since = System.currentTimeMillis() / 1000;
        List<Latency> sinceQueue = queue.stream()
                .filter(latency -> latency.getTimestamp() / 1000 == since)
                .collect(Collectors.toList());
        return statics(since * 1000, sinceQueue);
    }

    public static Statics staticsByMinute() {
        long since = System.currentTimeMillis() / 1000 / 60;
        List<Latency> sinceQueue = queue.stream()
                .filter(latency -> latency.getTimestamp() / 1000 / 60 == since)
                .collect(Collectors.toList());
        return statics(since * 60000, sinceQueue);
    }

    public static Statics staticsByHour() {
        long since = System.currentTimeMillis() / 1000 / 60 / 60;
        List<Latency> sinceQueue = queue.stream()
                .filter(latency -> latency.getTimestamp() / 1000 / 60 / 60 == since)
                .collect(Collectors.toList());
        return statics(since * 3600000, sinceQueue);
    }

    public static Mono<List<Statics>> staticsInHour() {
        long since = System.currentTimeMillis() / 1000 / 60 / 60;
        return Flux.range(0, 60)
                .map((i) -> {
                    long minute = since * 60 + i;
                    List<Latency> minuteQueue = queue.stream()
                            .filter(latency -> latency.getTimestamp() / 1000 / 60 == minute)
                            .collect(Collectors.toList());
                    return statics(minute * 60000, minuteQueue);
                }).collect(Collectors.toList());
    }

    private static float calcQps(List<Latency> latencyList) {
        int totalCount = latencyList.size();
        if (totalCount <= 0) {
            return 0;
        } else if (totalCount == 1) {
            return 1;
        } else {
            float span = (latencyList.get(totalCount - 1).getTimestamp() - latencyList.get(0).getTimestamp()) / 1000f;
            if (span < .001f) {
                // FIXME: 18/12/3
                span = .001f;
            }
            return totalCount / span;
        }
    }
}
