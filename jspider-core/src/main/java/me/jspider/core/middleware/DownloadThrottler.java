package me.jspider.core.middleware;

import me.jspider.base.bean.SpiderRequest;
import me.jspider.base.bean.SpiderResponse;
import me.jspider.core.setting.Setting;
import com.google.common.util.concurrent.RateLimiter;
import org.springframework.stereotype.Component;

import java.util.concurrent.Semaphore;

/**
 * Throttles the request, by qps and concurrency.
 */
@Component
public class DownloadThrottler implements Middleware {
    /**
     * Local concurrency control.
     */
    private Semaphore semaphore;
    /**
     * Local qps control.
     */
    private RateLimiter limiter;

    @Override
    public boolean processRequest(SpiderRequest request) {
        try {
            semaphore.acquire();
            limiter.acquire();
        } catch (InterruptedException ignored) { }
        return true;
    }

    @Override
    public boolean processResponse(SpiderResponse response) {
        semaphore.release();
        return true;
    }

    @Override
    public void open(Setting setting) {
        semaphore = new Semaphore(setting.getMiddlewareThrottlerConcurrency());
        limiter = RateLimiter.create(setting.getMiddlewareThrottlerQps());
    }

    @Override
    public void close() { }
}
