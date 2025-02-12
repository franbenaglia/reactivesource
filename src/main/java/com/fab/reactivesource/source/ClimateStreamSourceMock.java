package com.fab.reactivesource.source;

import java.time.Duration;
import java.util.Date;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

import org.springframework.stereotype.Component;

import com.fab.reactivesource.source.model.Climate;

import reactor.core.publisher.Flux;

@Component
public class ClimateStreamSourceMock {

    private static final long TIME_GAP = 500L;

    private static final float MAX_TEMP_AVERAGE = 30.0f;
    private static final float MIN_TEMP_AVERAGE = -5.0f;

    private static final float MAX_TEMP_VARIATION = 4.0f;
    private static final float MIN_TEMP_VARIATION = 4.0f;

    private static final float SLOPE_MAX = 200f;
    private static final float SLOPE_MIN = 150f;

    private float aleatorySign() {
        return Math.random() > 0.5 ? 1 : -1;
    }

    private double slope() {
        return Math.random() * (SLOPE_MAX - SLOPE_MIN);
    }

    private long randomLong() {
        return (long) (Math.random() * 100000);
    }

    private Climate maxBoundaryClimate(long channel) {

        double temperature = (Math.random() * MAX_TEMP_VARIATION) * aleatorySign() + MAX_TEMP_AVERAGE;
        double humidity = 50.0;

        return new Climate(temperature, humidity, channel, 0, new Date());
    }

    private Climate minBoundaryClimate(long channel) {

        double temperature = (Math.random() * MIN_TEMP_VARIATION * aleatorySign()) + MIN_TEMP_AVERAGE;
        double humidity = 40.0;

        return new Climate(temperature, humidity, channel, 0, new Date());
    }

    // https://www.npmjs.com/package/node-port-scanner
    public Flux<Climate> getClimateData() {

        long channel = randomLong();

        Climate maxBoundaryClimate = maxBoundaryClimate(channel);
        Climate minBoundaryClimate = minBoundaryClimate(channel);

        double slope = slope();

        var wrapper = new Object() {
            boolean rising = true;
            int idx = 0;
        };

        final double rate = (maxBoundaryClimate.temperature() - minBoundaryClimate.temperature()) / slope;

        UnaryOperator<Climate> opx = c -> {

            // if (wrapper.idx == 48) {
            // wrapper.idx = 0;
            // }

            // float v = aleatorySign();
            // v == 1 ? channel : 400
            if (c.temperature() < maxBoundaryClimate.temperature() && wrapper.rising) {
                return new Climate(c.temperature() + rate, c.humidity(), channel, wrapper.idx++, new Date());
            } else {
                wrapper.rising = false;
            }
            if (c.temperature() > minBoundaryClimate.temperature() && !wrapper.rising) {
                return new Climate(c.temperature() - rate, c.humidity(), channel, wrapper.idx++, new Date());
            } else {
                wrapper.rising = true;
                wrapper.idx = 0;
                return new Climate(c.temperature() - rate, c.humidity(), channel, wrapper.idx++, new Date());
            }

        };

        Stream<Climate> tempStream = Stream.iterate(minBoundaryClimate, opx);
        Flux<Climate> infiniteTempStream = Flux.fromStream(tempStream);

        return infiniteTempStream
                .delayElements(Duration.ofMillis(TIME_GAP));

    }

}
