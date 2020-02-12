package me.jspider.core.engine.monitor;

import me.jspider.core.setting.Setting;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * An implementation of {@link Monitor} which monitors the status of then engine and reports it periodically.
 */
@Slf4j
public class EngineStatMonitor implements Monitor {
    private static final String THREAD_NAME = "engine-stat-monitor-%d" ;

    private ScheduledExecutorService executorService;
    private Stat stat;

    @Override
    public void start(Setting setting) {
        executorService = Executors.newSingleThreadScheduledExecutor(
                new ThreadFactoryBuilder().setNameFormat(THREAD_NAME).build());
        stat = new Stat();
        executorService.scheduleAtFixedRate(this::report, 1, 1, TimeUnit.MINUTES);
    }

    @Override
    public void stop() {
        if (Objects.isNull(executorService)) {
            return;
        }
        executorService.shutdown();
        try {
            executorService.awaitTermination(1, TimeUnit.SECONDS);
        } catch (InterruptedException ignored) { }
        executorService.shutdownNow();
    }

    @Override
    public void onRequest() {
        stat.incrRequestCount();
    }

    @Override
    public void onSuccess() {
        stat.incrSuccessCount();
    }

    @Override
    public void onFailure() {
        stat.incrFailureCount();
    }

    @Override
    public void onItem() {
        stat.incrItemCount();
    }

    private void report() {
        long minute = stat.getRunningMinutes();
        log.info("Stat: running(min): {}, total(#): [request: {} success: {}, failure: {}, item: {}]; average(#/min): [request: {} success: {}, failure: {}, item: {}]",
                minute, stat.getRequestCount(), stat.getSuccessCount(), stat.getFailureCount(), stat.getItemCount(),
                stat.getRequestCount() * 1.0 / minute, stat.getSuccessCount() * 1.0 / minute, stat.getFailureCount() * 1.0 / minute, stat.getItemCount() * 1.0 / minute);
    }

    private static class Stat {
        private long startTime = System.currentTimeMillis();
        private AtomicInteger requestCount = new AtomicInteger(0);
        private AtomicInteger successCount = new AtomicInteger(0);
        private AtomicInteger failureCount = new AtomicInteger(0);
        private AtomicInteger itemCount = new AtomicInteger(0);

        public long getRunningMinutes() {
            return TimeUnit.MINUTES.convert(System.currentTimeMillis() - startTime, TimeUnit.MILLISECONDS);
        }

        public int getRequestCount() {
            return requestCount.get();
        }

        public int getSuccessCount() {
            return successCount.get();
        }

        public int getFailureCount() {
            return failureCount.get();
        }

        public int getItemCount() {
            return itemCount.get();
        }

        public void incrRequestCount() {
            requestCount.incrementAndGet();
        }

        public void incrSuccessCount() {
            successCount.incrementAndGet();
        }

        public void incrFailureCount() {
            failureCount.incrementAndGet();
        }

        public void incrItemCount() {
            itemCount.incrementAndGet();
        }
    }
}
