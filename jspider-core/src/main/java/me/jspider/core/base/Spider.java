package me.jspider.core.base;

import me.jspider.core.downloader.Downloader;
import me.jspider.core.downloader.HttpDownloader;
import me.jspider.core.middleware.Middleware;
import me.jspider.core.pipeline.ItemLogger;
import me.jspider.core.pipeline.Pipeline;
import me.jspider.core.scheduler.Scheduler;
import me.jspider.core.scheduler.SimpleScheduler;
import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies a spider.
 * For a spider, the name is required, while other components have default values.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Component
public @interface Spider {
    /**
     * @return Name of the spider.
     */
    String value();

    /**
     * @return The {@link Scheduler} to use.
     */
    Class<? extends Scheduler> scheduler() default SimpleScheduler.class;

    /**
     * @return The {@link Downloader} to use.
     */
    Class<? extends Downloader> downloader() default HttpDownloader.class;

    /**
     * @return The {@link Pipeline}s to use.
     */
    Class<? extends Pipeline>[] pipeline() default {ItemLogger.class};

    /**
     * @return The {@link Middleware}s to use.
     */
    Class<? extends Middleware>[] middleware() default {};
}
