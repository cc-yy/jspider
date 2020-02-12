package me.jspider.core.downloader;

import me.jspider.base.bean.SpiderRequest;
import me.jspider.base.bean.SpiderResponse;
import me.jspider.base.common.SpiderConfig;
import me.jspider.core.setting.Setting;
import com.google.common.collect.Queues;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * An implementation of {@link Downloader} based on selenium and chromedriver.
 * Note that limited by the driver, only url in the request matters in real http request; and that the response is always with HTTP OK and document body (maybo not expected one).
 * Reference: <a href="https://www.seleniumhq.org/docs/">Selenium Documentation</a>
 */
@Component
public class SeleniumDownloader implements Downloader {
    private static final String SYSTEM_PROPERTY_KEY = "webdriver.chrome.driver";
    private static final int MAX_DRIVER_COUNT = 10;

    private SpiderConfig spiderConfig;
    private ArrayBlockingQueue<ChromeDriver> drivers;
    private volatile boolean closed = true;


    @Override
    public SpiderResponse downloadSync(SpiderRequest request) {
        if (closed) {
            return SpiderResponse.fail(request);
        }
        ChromeDriver driver = borrowDriver();
        if (Objects.isNull(driver)) {
            return SpiderResponse.fail(request);
        }
        try {
            driver.get(request.getUrl());
            new WebDriverWait(driver, 5).until(d -> StringUtils.isNoneBlank(d.getTitle()));
            return SpiderResponse.builder().url(driver.getCurrentUrl())
                    .body(driver.getPageSource().getBytes(StandardCharsets.UTF_8)).status(200)
                    .request(request).build();
        } catch (Exception e) {
            return SpiderResponse.fail(request);
        } finally {
            returnDriver(driver);
        }
    }

    @Override
    public CompletableFuture<SpiderResponse> downloadAsync(SpiderRequest request) {
        throw new UnsupportedOperationException("async operation is not allowed");
    }

    @Override
    public void start(Setting setting) {
        if (!closed) {
            return;
        }
        if (Objects.nonNull(spiderConfig)) {
            System.setProperty(SYSTEM_PROPERTY_KEY, spiderConfig.getChromeDriver());
        }
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--incognito"
//                "--user-agent=Mozilla/5.0 (Linux; <Android Version>; <Build Tag etc.>) AppleWebKit/<WebKit Rev> (KHTML, like Gecko) Chrome/<Chrome Rev> Mobile Safari/<WebKit Rev>",
//                "--window-size=720,1080"
        );

        drivers = Queues.newArrayBlockingQueue(Math.min(MAX_DRIVER_COUNT, setting.getEngineThreadCount()));
        while (drivers.remainingCapacity() > 0) {
            drivers.add(new ChromeDriver(options));
        }
        closed = false;
    }

    @Override
    public void stop() {
        if (closed) {
            return;
        }
        closed = true;
        ChromeDriver driver;
        while ((driver = borrowDriver()) != null) {
            driver.quit();
        }
    }

    @Autowired
    public void setSpiderConfig(SpiderConfig spiderConfig) {
        this.spiderConfig = spiderConfig;
    }

    private ChromeDriver borrowDriver() {
        while (!closed) {
            try {
                ChromeDriver driver = drivers.poll(100, TimeUnit.MILLISECONDS);
                if (Objects.nonNull(driver)) {
                    return driver;
                }
            } catch (InterruptedException ignored) { }
        }
        return null;
    }

    private void returnDriver(ChromeDriver driver) {
        while (!closed) {
            try {
                drivers.offer(driver, 100, TimeUnit.MILLISECONDS);
                return;
            } catch (InterruptedException ignored) { }
        }
    }
}
