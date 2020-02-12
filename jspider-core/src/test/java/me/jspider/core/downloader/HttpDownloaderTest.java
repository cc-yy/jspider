package me.jspider.core.downloader;

import me.jspider.core.mock.HttpServer;
import me.jspider.core.setting.Setting;
import me.jspider.base.bean.SpiderRequest;
import me.jspider.base.bean.SpiderResponse;
import me.jspider.base.common.SpiderTestConstant;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled("network required")
public class HttpDownloaderTest {
    private static final HttpServer server = new HttpServer(SpiderTestConstant.HTTP_PORT);
    private HttpDownloader downloader;

    @BeforeAll
    public static void setupAll() {
        server.start();
    }

    @AfterAll
    public static void cleanupAll() {
        server.stop();
    }

    @BeforeEach
    public void setup() {
        downloader = new HttpDownloader();
        downloader.start(new Setting());
    }

    @Test
    public void testDownload_Real() {
        SpiderRequest request = SpiderRequest.builder().url("http://localhost:" + SpiderTestConstant.HTTP_PORT + SpiderTestConstant.HTTP_OK_PATH).build();
        request.setHeader(HttpHeaders.ACCEPT, "html");
        request.setHeader(HttpHeaders.ACCEPT_ENCODING, "plain");
        SpiderResponse response = downloader.downloadSync(request);

        Assertions.assertEquals(request.getUrl(), response.getUrl());
        Assertions.assertEquals(HttpStatus.SC_OK, response.getStatus());
        Assertions.assertNotEquals(0, response.getBody().length);
    }
}
