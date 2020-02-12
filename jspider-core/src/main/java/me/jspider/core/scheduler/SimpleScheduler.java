package me.jspider.core.scheduler;

import me.jspider.base.bean.SpiderDataItem;
import me.jspider.base.bean.SpiderRequest;
import me.jspider.base.bean.SpiderResponse;
import me.jspider.core.setting.Setting;
import com.google.common.collect.Queues;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A simple implementation of {@link Scheduler} based on {@link BlockingQueue}.
 */
@Slf4j
@Component
public class SimpleScheduler implements Scheduler {
    private static final int MAX_SIZE = 20000;
    private static final int POLL_TIMEOUT = 500;

    private BlockingQueue<SpiderRequest> requests = Queues.newArrayBlockingQueue(MAX_SIZE);
    private BlockingQueue<SpiderResponse> responses = Queues.newArrayBlockingQueue(MAX_SIZE);
    private BlockingQueue<SpiderDataItem> items = Queues.newLinkedBlockingQueue();

    private AtomicBoolean closed = new AtomicBoolean(true);

    @Override
    public void start(Setting setting) {
        closed.set(false);
    }

    @Override
    public void stop() {
        closed.set(true);
    }

    @Override
    public void offerRequest(SpiderRequest request) {
        offer(requests, request);
    }

    @Override
    public SpiderRequest takeRequest() {
        return take(requests);
    }

    @Override
    public void offerResponse(SpiderResponse response) {
        offer(responses, response);
    }

    @Override
    public SpiderResponse takeResponse() {
        return take(responses);
    }

    @Override
    public void offerItem(SpiderDataItem item) {
        offer(items, item);
    }

    @Override
    public SpiderDataItem takeItem() {
        return take(items);
    }

    private <T> void offer(BlockingQueue<T> queue, T item) {
        while (!closed.get()) {
            try {
                queue.put(item);
                return;
            } catch (InterruptedException ignored) { }
        }
    }

    private <T> T take(BlockingQueue<T> queue) {
        while (!queue.isEmpty() && !closed.get()) {
            try {
                return queue.poll(POLL_TIMEOUT, TimeUnit.MILLISECONDS);
            } catch (InterruptedException ignored) { }
        }
        return null;
    }
}
