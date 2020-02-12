package me.jspider.core.engine;

import me.jspider.base.bean.SpiderDataItem;
import me.jspider.base.bean.SpiderItem;
import me.jspider.base.bean.SpiderRequest;
import me.jspider.base.bean.SpiderResponse;
import me.jspider.core.middleware.RequestNormalizer;
import me.jspider.core.pipeline.PipelineChain;
import me.jspider.core.engine.monitor.EngineStatMonitor;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

/**
 * Engine.
 */
@Slf4j
public class ThreadedEngine extends AbstractEngine {
    private DownloadWorker worker;

    private ExecutorService executorService;

    private ThreadedEngine(boolean shared) {
        super(shared);
        init();
    }

    public static ThreadedEngine create() {
        return new ThreadedEngine(false);
    }

    public static ThreadedEngine createShared() {
        return new ThreadedEngine(true);
    }

    @Override
    public void runAsync() {
        executorService.execute(this::pipeline);
        int threadCount = setting.getEngineThreadCount() - 1;
        int pc = Math.min((int) Math.floor(threadCount * 0.5), threadCount - 2);
        int dc = setting.isEngineDownloadAsync() ? 1 : threadCount - pc;
        IntStream.range(0, pc).forEach(i -> executorService.execute(this::parse));
        IntStream.range(0, dc).forEach(i -> executorService.execute(this::download));
    }

    private void init() {
        monitors.add(new EngineStatMonitor());

        middlewares.add(new RequestNormalizer());
    }

    @Override
    protected void prepare() {
        Preconditions.checkArgument(setting.getEngineThreadCount() >= 4, "thread count too small");

        executorService = Executors.newFixedThreadPool(setting.getEngineThreadCount(),
                new ThreadFactoryBuilder().setNameFormat("engine-%d").build());

        worker = setting.isEngineDownloadAsync() ? new AsyncWorker() : new SyncWorker();
    }

    @Override
    protected void cleanup() {
        executorService.shutdown();
        try {
            executorService.awaitTermination(4, TimeUnit.SECONDS);
        } catch (InterruptedException ignored) {
        }
        executorService.shutdownNow();

        SpiderDataItem item = scheduler.takeItem();
        while (Objects.nonNull(item)) {
            PipelineChain chain = new PipelineChain(Lists.newArrayList(pipelines.iterator()));
            chain.process(item);
            item = scheduler.takeItem();
        }
    }

    private void download() {
        while (active.get()) {
            SpiderRequest request = scheduler.takeRequest();
            if (Objects.isNull(request)) {
                continue;
            }

            if (!callMiddleware(request)) {
                onRequestDone();
            }

            worker.doWork(request);
        }
    }

    private void parse() {
        while (active.get()) {
            SpiderResponse response = scheduler.takeResponse();
            if (Objects.isNull(response)) {
                continue;
            }

            if (Objects.isNull(response.getRequest().getParser())) {
                log.warn("No parser bound to the request: {}", response.getRequest().getUrl());
                continue;
            }
            Iterable<SpiderItem> items;
            try {
                items = response.getRequest().getParser().parse(response);
            } catch (Exception e) {
                log.error("Failed to parse response: {}", response.getUrl(), e);
                items = Collections.emptyList();
            }

            for (SpiderItem item : items) {
                if (item instanceof SpiderRequest) {
                    onRequest((SpiderRequest) item);
                } else if (item instanceof SpiderDataItem) {
                    onItem((SpiderDataItem) item);
                } else {
                    log.warn("Unknown item: {}.", item);
                }
            }
            onRequestDone();
        }
    }

    private void pipeline() {
        while (active.get()) {
            SpiderDataItem item = scheduler.takeItem();
            if (Objects.isNull(item)) {
                continue;
            }

            callPipeline(item);
        }
    }

    private interface DownloadWorker {
        void doWork(SpiderRequest request);
    }

    private class SyncWorker implements DownloadWorker {
        @Override
        public void doWork(SpiderRequest request) {
            SpiderResponse response = downloader.downloadSync(request);
            if (!callMiddleware(response)) {
                onRequestDone();
            }
            onResponse(response);
        }
    }

    private class AsyncWorker implements DownloadWorker {
        @Override
        public void doWork(SpiderRequest request) {
            CompletableFuture<SpiderResponse> future = downloader.downloadAsync(request);
            future.whenCompleteAsync((response, throwable) -> {
                if (!callMiddleware(response)) {
                    onRequestDone();
                }
                onResponse(response);
            }, executorService);
        }
    }
}
