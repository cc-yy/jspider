package me.jspider.core;

import me.jspider.base.common.SpiderConstant;
import me.jspider.core.base.Spider;
import me.jspider.core.engine.AbstractEngine;
import me.jspider.core.engine.ThreadedEngine;
import me.jspider.core.middleware.Middleware;
import me.jspider.core.pipeline.Pipeline;
import me.jspider.core.setting.Setting;
import me.jspider.core.spider.AbstractSpider;
import me.jspider.base.bean.SpiderException;
import me.jspider.base.bean.SpiderRuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.AssignableTypeFilter;

import java.util.Objects;

@Slf4j
public class Crawler {
    private AbstractEngine engine;
    private Setting setting;
    private AbstractSpider spider;

    private Crawler () {}

    public static Crawler create(AbstractSpider spider) {
        Crawler crawler = new Crawler();
        crawler.engine = ThreadedEngine.create();
        crawler.setting = new Setting();
        crawler.spider = spider;
        return crawler;
    }

    public static Crawler create(Setting setting, AbstractSpider spider) {
        Crawler crawler = new Crawler();
        crawler.engine = ThreadedEngine.create();
        crawler.setting = setting;
        crawler.spider = spider;
        return crawler;
    }

    public Crawler init(ComponentFactory factory) throws SpiderException {
        try {
            Spider anno = spider.getClass().getAnnotation(Spider.class);
            engine.setDownloader(factory.get(anno.downloader()));
            engine.setScheduler(factory.get(anno.scheduler()));
            if (ArrayUtils.isNotEmpty(anno.pipeline())) {
                for (Class<? extends Pipeline> c : anno.pipeline()) {
                    engine.addPipeline(factory.get(c));
                }
            }
            if (ArrayUtils.isNotEmpty(anno.middleware())) {
                for (Class<? extends Middleware> c : anno.middleware()) {
                    engine.addMiddleware(factory.get(c));
                }
            }
            return this;
        } catch (Exception e) {
            throw new SpiderException("Failed to init crawler", e);
        }
    }

    public void crawl() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> engine.stop()));

        spider.init(setting);
        engine.start(setting);

        engine.runAsync();
        spider.seed().forEach(engine::addRequest);
        engine.waitForCompletion();

        System.exit(0);
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: xxx spider-name");
            System.exit(1);
        }
        AbstractSpider spider = findSpider(args[0]);
        if (Objects.isNull(spider)) {
            System.out.println("Spider not found: " + args[0]);
            System.exit(1);
        }
        System.out.println("Active spider: " + args[0]);
        try {
            Crawler.create(spider).init(Class::newInstance).crawl();
        } catch (SpiderException | SpiderRuntimeException e) {
            log.error("Failed to start crawler", e);
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }

    @SuppressWarnings("unchecked")
    private static AbstractSpider findSpider(String spiderName) {
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(Spider.class));
        scanner.addIncludeFilter(new AssignableTypeFilter(AbstractSpider.class));
        for (BeanDefinition def : scanner.findCandidateComponents(SpiderConstant.BASE_PACKAGE_NAME)) {
            try {
                Class<?> clazz = Class.forName(def.getBeanClassName());
                Spider s = clazz.getAnnotation(Spider.class);
                if (s.value().equals(spiderName)) {
                    return ((Class<? extends AbstractSpider>) clazz).newInstance();
                }
            } catch (Exception ignored) { }
        }

        return null;
    }

    @FunctionalInterface
    public interface ComponentFactory {
        <T> T get(Class<T> clazz) throws Exception;
    }
}
