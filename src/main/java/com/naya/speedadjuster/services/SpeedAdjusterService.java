package com.naya.speedadjuster.services;

import com.naya.speedadjuster.AdjustmentProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Evgeny Borisov
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SpeedAdjusterService {
    private final RestTemplate restTemplate;
    private final AdjustmentProperties adjustmentProperties;
    private AtomicLong lastDelay = new AtomicLong(Long.MIN_VALUE);
    private AtomicLong delta = new AtomicLong(10);

    public void changeDelta(long newDelta) {
        delta.set(newDelta);
    }


    public void newDelay(long delay) {
        if (lastDelay.get() - delay > delta.get()) {
            lastDelay.set(delay);
            requestForChangeSpeed(delay);
        }
    }

    private void requestForChangeSpeed(long delay) {
        try {
            restTemplate.getForObject(adjustmentProperties.getUrl() + "/" + delay / adjustmentProperties.getNumberOfThreads(), ResponseEntity.class);
        } catch (RestClientException e) {
            log.error("no sender url found");
        }
        log.info("delay was requested for " + delay);
    }
}
