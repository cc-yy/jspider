package me.jspider.core.engine;

import me.jspider.core.downloader.HttpDownloader;
import me.jspider.core.middleware.Middleware;
import me.jspider.core.mock.HttpServer;
import me.jspider.core.pipeline.Pipeline;
import me.jspider.core.scheduler.SimpleScheduler;
import me.jspider.core.setting.Setting;
import com.google.common.collect.Lists;
import me.jspider.base.bean.SpiderDataItem;
import me.jspider.base.bean.SpiderRequest;
import me.jspider.base.bean.SpiderResponse;
import me.jspider.base.common.SpiderTestConstant;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
public class AbstractEngineTest extends Mockito {
    private static final HttpServer server = new HttpServer(SpiderTestConstant.HTTP_PORT);
    private static final String HOST = "http://127.0.0.1:" + SpiderTestConstant.HTTP_PORT;

    @Mock
    private Middleware middleware;
    @Mock
    private Pipeline pipeline;

    private final Item item = new Item();
    private ThreadedEngine engine;

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
        engine = ThreadedEngine.create();
        engine.setDownloader(new HttpDownloader()).setScheduler(new SimpleScheduler())
                .addMiddleware(middleware).addPipeline(pipeline);

        when(middleware.processRequest(any(SpiderRequest.class))).thenReturn(true);
        when(middleware.processResponse(any(SpiderResponse.class))).thenReturn(true);
    }

    @Test
    public void test_Normal() {
        String url = HOST + SpiderTestConstant.HTTP_OK_PATH;

        engine.start(new Setting());
        engine.addRequest(SpiderRequest.builder().url(url).parser(r -> Lists.newArrayList(item)).build());
        engine.run();
        engine.stop();

        verify(middleware).open(any());
        verify(middleware).processRequest(any(SpiderRequest.class));
        verify(middleware).processResponse(argThat(a -> a.getStatus() == 200));
        verify(middleware).close();

        verify(pipeline).open(any());
        verify(pipeline).process(eq(item), any());
        verify(pipeline).close();
    }

    @Test
    public void test_Fail() {
        String url = HOST + SpiderTestConstant.HTTP_NOT_FOUND_PATH;

        engine.start(new Setting());
        engine.addRequest(SpiderRequest.builder().url(url).parser(r -> Lists.newArrayList(item)).build());
        engine.run();
        engine.stop();

        verify(middleware).open(any());
        verify(middleware, times(3)).processRequest(any(SpiderRequest.class));
        verify(middleware, times(3)).processResponse(argThat(a -> a.getStatus() > 400));
        verify(middleware).close();

        InOrder order = inOrder(pipeline);
        order.verify(pipeline).open(any());
        order.verify(pipeline).close();
        order.verifyNoMoreInteractions();
    }

    private static class Item implements SpiderDataItem { }
}
