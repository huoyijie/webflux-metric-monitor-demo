package com.example.demo.metrics;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

/**
 * Created by huoyijie on 18/12/3.
 * qps, latency, success rate, saturation
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Statics {
    private int totalCount = 0;
    private float qps = 0f;
    private double avgCost = 0;
    private double pct90Cost = 0;
    private double pct99Cost = 0;
    private float successRate = 0;
    private String since;
}
