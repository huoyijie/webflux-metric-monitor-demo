package com.example.demo;

import com.example.demo.metrics.Metric;
import com.example.demo.metrics.MetricContainer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Hooks;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import static org.springframework.web.reactive.function.BodyInserters.fromObject;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;

@SpringBootApplication
//fixme @EnableWebFlux
public class WebfluxDemoApplication {

	public static void main(String[] args) {
		Hooks.onOperatorDebug();
		SpringApplication.run(WebfluxDemoApplication.class, args);
	}

	@Bean
	public RouterFunction<ServerResponse> routerFunction() {
		return RouterFunctions
				.route(GET("/demo"), serverRequest -> ServerResponse.ok().body(fromObject("Demo OK")))
				.andRoute(GET("/"), serverRequest -> ServerResponse.ok().render("index"))
				.andRoute(GET("/queue"), serverRequest -> ServerResponse.ok().body(fromObject(MetricContainer.queue)))
				.andRoute(GET("/statics/second"), serverRequest -> ServerResponse.ok().body(fromObject(MetricContainer.staticsBySecond())))
                .andRoute(GET("/statics/minute"), serverRequest -> ServerResponse.ok().body(fromObject(MetricContainer.staticsByMinute())))
                .andRoute(GET("/statics"), serverRequest -> ServerResponse.ok().body(fromObject(MetricContainer.staticsByHour())))
                .andRoute(GET("/api/statics/hour"), serverRequest -> MetricContainer.staticsInHour()
                        .map(staticsInHour -> ServerResponse.ok().body(fromObject(staticsInHour)).block()))
				.filter((serverRequest, handlerFunction) -> {
                    Map<String, String> tags = new LinkedHashMap<>();
                    tags.put("method", serverRequest.methodName());
                    tags.put("uuid", UUID.randomUUID().toString());
                    return Metric.latency(
                            "http.request",
                            serverRequest.path(),
                            () -> handlerFunction.handle(serverRequest), tags);
                });
	}
}
