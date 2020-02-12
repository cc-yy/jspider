package me.jspider.core.downloader;

import me.jspider.base.bean.SpiderRequest;
import me.jspider.base.bean.SpiderResponse;
import me.jspider.core.base.StartStoppable;

import java.util.concurrent.CompletableFuture;

public interface Downloader extends StartStoppable {

    /**
     * Download the request and return the response. An synchronous call.
     *
     * @param request The source request.
     * @return The downloaded response.
     */
    SpiderResponse downloadSync(SpiderRequest request);

    /**
     * Download the request asynchronously.
     *  @param request The source request.
     *
     */
    CompletableFuture<SpiderResponse> downloadAsync(SpiderRequest request);
}
