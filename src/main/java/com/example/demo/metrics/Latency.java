package com.example.demo.metrics;

import lombok.*;

import java.util.Map;

/**
 * Created by huoyijie on 18/11/30.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Latency {
    private long timestamp;
    private String metricName;
    private String targetName;
    private String status;
    private Map<String, String> tags;
    private float cost;

    public boolean succeed() {
        return "success".equals(status);
    }
}
