package me.jspider.core.downloader;

import me.jspider.base.bean.SpiderRequest;
import me.jspider.base.bean.SpiderResponse;
import me.jspider.core.setting.Setting;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.util.StreamUtils;

import java.io.FileOutputStream;
import java.io.IOException;

@Disabled("network required")
public class SeleniumDownloaderTest {
    private SeleniumDownloader downloader;

    @BeforeEach
    public void setup() {
        System.setProperty("webdriver.chrome.driver", "/Users/luoyi04/Downloads/chromedriver");
        downloader = new SeleniumDownloader();
        downloader.start(new Setting());
    }

    @Test
    public void testDownload() throws IOException {
        SpiderResponse response = downloader.downloadSync(SpiderRequest.builder().url("https://union-click.jd.com/sem.php?source=baidu-nks&unionId=262767352&siteId=baidunks_{keywordid}&playId={pa_mt_id}&ext=11116650041&adOwner={pa_exp}&to=https%3A%2F%2Fitem%2Ejd%2Ecom%2F11116650041%2Ehtml%3Fbd_vid%3D{BD_VID}").build());
        StreamUtils.copy(response.getBody(), new FileOutputStream("1.html"));
    }

    @AfterEach
    public void cleanup() {
        downloader.stop();
    }
}
