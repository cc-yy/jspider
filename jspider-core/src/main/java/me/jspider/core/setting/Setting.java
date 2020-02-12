package me.jspider.core.setting;

import me.jspider.base.common.SpiderConstant;
import me.jspider.core.downloader.HttpDownloader;
import me.jspider.core.middleware.DownloadThrottler;
import me.jspider.core.pipeline.exporter.BaseFileExporter;
import me.jspider.core.pipeline.filter.BaseItemFilter;
import me.jspider.core.engine.AbstractEngine;
import me.jspider.core.engine.ThreadedEngine;
import com.google.common.collect.Lists;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Properties;

/**
 * Holds all settings for the spider.
 */
@Getter
@Setter
public class Setting {
    public static final String ENGINE_MAX_RETRY = "spider.setting.engine.max.retry";
    public static final String ENGINE_THREAD_COUNT = "spider.setting.engine.thread.count";
    public static final String ENGINE_STATUS_CODE_ACCEPT = "spider.setting.engine.status.code.accept";
    public static final String ENGINE_STATUS_CODE_REDIRECT = "spider.setting.engine.status.code.redirect";
    public static final String ENGINE_DOWNLOAD_ASYNC = "spider.setting.engine.download.async";
    public static final String DOWNLOADER_TIMEOUT = "spider.setting.downloader.timeout";
    public static final String PIPELINE_EXPORTER_OUTPUT_FILE = "spider.setting.pipeline.exporter.output.file";
    public static final String PIPELINE_EXPORTER_OUTPUT_CHARSET = "spider.setting.pipeline.exporter.output.charset";
    public static final String PIPELINE_EXPORTER_ITEM_FIELDS = "spider.setting.pipeline.exporter.item.fields";
    public static final String PIPELINE_FILTER_EXPRESSION = "spider.setting.pipeline.filter.expression";
    public static final String MIDDLEWARE_THROTTLER_CONCURRENCY = "spider.setting.middleware.throttler.concurrency";
    public static final String MIDDLEWARE_THROTTLER_QPS = "spider.setting.middleware.throttler.qps";

    /**
     * For {@link AbstractEngine}, max retry.
     */
    @Value("${" + ENGINE_MAX_RETRY + ":3}")
    private int engineMaxRetry = 3;
    /**
     * For {@link ThreadedEngine}, thread count for main flow (request -> response -> item, at least 3.
     */
    @Value("${" + ENGINE_THREAD_COUNT + ":4}")
    private int engineThreadCount = Math.max(4, Runtime.getRuntime().availableProcessors());
    /**
     * For {@link AbstractEngine}, http status codes of which the response are accepted, ie. success.
     */
    @Value("${" + ENGINE_STATUS_CODE_ACCEPT + ":200}")
    private List<Integer> engineStatusCodeAccept = Lists.newArrayList(200);
    /**
     * For {@link AbstractEngine}, http status codes of which the response should be redirected.
     * Note: {@link HttpDownloader} also handles redirect.
     */
    @Value("${"+  ENGINE_STATUS_CODE_REDIRECT + ":301,302,303,307,308}")
    private List<Integer> engineStatusCodeRedirect = Lists.newArrayList(301, 302, 303, 307, 308);
    /**
     * For {@link AbstractEngine}, indicates to download in asynchronous mode.
     */
    @Value("${"+  ENGINE_DOWNLOAD_ASYNC + ":false}")
    private boolean engineDownloadAsync = false;
    /**
     * Timeout for downloader, in second.
     */
    @Value("${" + DOWNLOADER_TIMEOUT + ":10}")
    private int downloaderTimeout = 10;

    /**
     * For {@link BaseFileExporter} and its subclasses, name of output file.
     */
    @Value("${" + PIPELINE_EXPORTER_OUTPUT_FILE + ":./out.txt}")
    private String pipelineExporterOutputFile = "./out.txt";
    /**
     * For {@link BaseFileExporter} and its subclasses, charset of output file.
     */
    @Value("${" + PIPELINE_EXPORTER_OUTPUT_CHARSET + ":UTF-8}")
    @Setter(AccessLevel.NONE)
    private Charset pipelineExporterOutputCharset = SpiderConstant.DEFAULT_CHARSET;
    /**
     * For {@link BaseFileExporter} and its subclasses, indicates which fields in the item should be exported.
     */
    @Value("${" + PIPELINE_EXPORTER_ITEM_FIELDS + ":null}")
    private List<String> pipelineExporterItemFields = null;
    /**
     * For {@link BaseItemFilter} and its subclasses, items against which the expression is evaluated to be true are dropped.
     * Depending on implementations, the expression may varies in SpEL, freemarker, regex, etc.
     */
    @Value("${" + PIPELINE_FILTER_EXPRESSION + ":null}")
    private String pipelineFilterExpression = null;

    /**
     * For {@link DownloadThrottler}, max number of concurrent requests.
     */
    @Value("${" + MIDDLEWARE_THROTTLER_CONCURRENCY + ":10}")
    private int middlewareThrottlerConcurrency = 10;
    /**
     * For {@link DownloadThrottler}, max requests per second, qps.
     */
    @Value("${" + MIDDLEWARE_THROTTLER_QPS + ":10}")
    private int middlewareThrottlerQps = 10;

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private Properties properties;

    public void setPipelineExporterOutputCharset(String text) {
        this.pipelineExporterOutputCharset = Charset.forName(text);
    }

    public Object get(String key) {
        return properties.get(key);
    }

    public void set(String key, Object value) {
        properties.put(key, value);
    }
}
