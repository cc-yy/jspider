package me.jspider.core.engine;

import me.jspider.base.bean.SpiderDataItem;
import me.jspider.base.bean.SpiderRequest;
import me.jspider.base.bean.SpiderResponse;
import me.jspider.core.downloader.Downloader;
import me.jspider.core.middleware.Middleware;
import me.jspider.core.pipeline.Pipeline;
import me.jspider.core.pipeline.PipelineChain;
import me.jspider.core.scheduler.Scheduler;
import me.jspider.core.setting.Setting;
import me.jspider.core.base.OrderedChain;
import me.jspider.base.bean.SpiderRuntimeException;
import me.jspider.core.engine.monitor.Monitor;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHeaders;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Base engine.
 */
@Slf4j
public abstract class AbstractEngine {
    private static final int SLEEP_INTERVAL = 500;
    private final boolean shared;

    protected final AtomicBoolean active = new AtomicBoolean(false);
    protected final AtomicBoolean closed = new AtomicBoolean(true);
    protected final AtomicInteger running = new AtomicInteger(0);

    protected Setting setting;

    protected Scheduler scheduler;
    protected Downloader downloader;

    protected OrderedChain<Pipeline> pipelines = new OrderedChain<>();
    protected OrderedChain<Middleware> middlewares = new OrderedChain<>();

    protected List<Monitor> monitors = Lists.newArrayList();

    public AbstractEngine(boolean shared) {
        this.shared = shared;
    }

    public AbstractEngine setScheduler(Scheduler scheduler) {
        Preconditions.checkNotNull(scheduler);
        ensureState(false);
        this.scheduler = scheduler;
        return this;
    }

    public AbstractEngine setDownloader(Downloader downloader) {
        Preconditions.checkNotNull(downloader);
        ensureState(false);
        this.downloader = downloader;
        return this;
    }

    public AbstractEngine addPipeline(Pipeline pipeline) {
        Preconditions.checkNotNull(pipeline);
        ensureState(false);
        this.pipelines.add(pipeline);
        return this;
    }

    public AbstractEngine addMonitor(Monitor monitor) {
        Preconditions.checkNotNull(monitor);
        ensureState(false);
        this.monitors.add(monitor);
        return this;
    }

    public AbstractEngine addMiddleware(Middleware middleware) {
        Preconditions.checkNotNull(middleware);
        ensureState(false);
        this.middlewares.add(middleware);
        return this;
    }

    public void start(Setting setting) {
        if (!closed.get()) {
            return;
        }
        log.info("Engine starting components.");

        this.setting = setting;
        checkComponents();
        closed.set(false);

        prepare();
        if (!shared) {
            initComponents();
        }
        active.set(true);
        log.info("Engine started.");
    }

    public void stop() {
        if (closed.get()) {
            return;
        }
        log.info("Engine doing cleanup.");
        active.set(false);

        cleanup();
        if (!shared) {
            destroyComponents();
        }
        closed.set(true);
        log.info("Engine stopped.");
    }

    public AbstractEngine addRequest(SpiderRequest request) {
        Preconditions.checkNotNull(request);
        ensureState(true);
        onRequest(request);
        return this;
    }

    public void run() {
        runAsync();
        waitForCompletion();
    }
    
    public abstract void runAsync();

    public void runForever() {
        runAsync();

        while (!closed.get()) {
            try {
                Thread.sleep(SLEEP_INTERVAL);
            } catch (InterruptedException ignored) {
            }
        }
    }
    
    public void waitForCompletion() {
        while (active.get()) {
            try {
                Thread.sleep(SLEEP_INTERVAL);
            } catch (InterruptedException ignored) { }
            if (running.get() <= 0) {
                active.set(false);
                return;
            }
        }
    }

    private void checkComponents() {
        if (Objects.isNull(downloader)) {
            throw new SpiderRuntimeException("downloader not specified");
        }
        if (Objects.isNull(scheduler)) {
            throw new SpiderRuntimeException("scheduler not specified.");
        }
    }

    private void initComponents() {
        downloader.start(setting);
        scheduler.start(setting);
        monitors.forEach(m -> m.start(setting));
        middlewares.forEach(m -> m.open(setting));
        pipelines.forEach(p -> p.open(setting));
    }

    protected abstract void prepare();

    protected abstract void cleanup();

    private void destroyComponents() {
        downloader.stop();
        scheduler.stop();
        monitors.forEach(Monitor::stop);
        middlewares.forEach(Middleware::close);
        pipelines.forEach(Pipeline::close);
    }

    protected void onRequest(SpiderRequest request) {
        scheduler.offerRequest(request);
        running.incrementAndGet();
        monitors.forEach(Monitor::onRequest);
    }

    protected void onResponse(SpiderResponse response) {
        log.debug("Receive response: <{}> {}", response.getStatus(), response.getUrl());
        if (setting.getEngineStatusCodeAccept().contains(response.getStatus())) {
            onSuccess(response);
        } else if (setting.getEngineStatusCodeRedirect().contains(response.getStatus())){
            onRedirect(response);
        } else {
            onFailure(response);
        }
    }

    protected void onSuccess(SpiderResponse response) {
        monitors.forEach(Monitor::onSuccess);

        scheduler.offerResponse(response);
    }

    protected void onFailure(SpiderResponse response) {
        monitors.forEach(Monitor::onFailure);

        SpiderRequest request = response.getRequest();
        request.incrRetry();
        if (request.getRetry() <= setting.getEngineMaxRetry()) {
            log.debug("Retry request: {}", request.getUrl());
            onRequest(request);
        } else {
            log.debug("Failed request detected: {}", request.getUrl());
        }
        onRequestDone();
    }

    protected void onRedirect(SpiderResponse response) {
        String loc = response.getHeaders().get(HttpHeaders.LOCATION);
        if (StringUtils.isBlank(loc)) {
            onFailure(response);
        } else {
            try {
                loc = new URL(new URL(response.getUrl()), loc).toString();
            } catch (MalformedURLException e) {
                return;
            }
            SpiderRequest request = response.getRequest();
            log.debug("Redirect from {} to {}", request.getUrl(), loc);
            request.setUrl(loc);
            onRequest(request);
        }
        onRequestDone();
    }

    protected void onItem(SpiderDataItem item) {
        monitors.forEach(Monitor::onItem);
        scheduler.offerItem(item);
    }

    protected void onRequestDone() {
        running.decrementAndGet();
    }

    protected boolean callMiddleware(SpiderRequest request) {
        for (Middleware middleware : this.middlewares) {
            try {
                if (!middleware.processRequest(request)) {
                    return false;
                }
            } catch (Exception e) {
                log.error("Error occurred. request={} middleware={}", request, middleware.getClass().getSimpleName());
                return false;
            }
        }
        return true;
    }

    protected boolean callMiddleware(SpiderResponse response) {
        for (Middleware middleware : this.middlewares) {
            try {
                if (!middleware.processResponse(response)) {
                    return false;
                }
            } catch (Exception e) {
                log.error("Error occurred. response={} middleware={}", response, middleware.getClass().getSimpleName());
                return false;
            }
        }
        return true;
    }

    protected void callPipeline(SpiderDataItem item) {
        PipelineChain chain = new PipelineChain(pipelines);
        chain.process(item);
    }

    private void ensureState(boolean state) {
        if (active.get() != state) {
            if (active.get()) {
                throw new SpiderRuntimeException("already started");
            } else {
                throw new SpiderRuntimeException("not started yet");
            }
        }
    }
}
