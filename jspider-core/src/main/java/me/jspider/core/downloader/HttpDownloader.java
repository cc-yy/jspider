package me.jspider.core.downloader;

import me.jspider.base.bean.SpiderRequest;
import me.jspider.base.bean.SpiderResponse;
import me.jspider.base.common.SpiderConstant;
import me.jspider.base.bean.SpiderException;
import me.jspider.base.bean.SpiderRuntimeException;
import me.jspider.core.setting.Setting;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * An implementation of {@link Downloader} which is based on apache http client.
 * Reference: <a href="http://hc.apache.org/">HttpComponents Documentation</a>
 */
@Slf4j
@Component
public class HttpDownloader implements Downloader {
    private static final String GET = "GET";
    private static final String POST = "POST";

    private PoolingHttpClientConnectionManager connectionManager;
    private CloseableHttpClient httpClient;
    private CloseableHttpAsyncClient httpAsyncClient;

    private ConcurrentMap<String, HttpClientContext> contexts;

    @Override
    public void start(Setting setting) {
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout((int) TimeUnit.SECONDS.toMillis(setting.getDownloaderTimeout()))
                .setConnectTimeout((int) TimeUnit.SECONDS.toMillis(setting.getDownloaderTimeout()))
                .setSocketTimeout((int) TimeUnit.SECONDS.toMillis(setting.getDownloaderTimeout()))
                .build();

        connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(setting.getEngineThreadCount());
        httpClient = HttpClientBuilder.create().setDefaultRequestConfig(requestConfig)
                .setConnectionManager(connectionManager).build();

        httpAsyncClient = HttpAsyncClientBuilder.create().build();
        httpAsyncClient.start();

        contexts = Maps.newConcurrentMap();
    }

    @Override
    public void stop() {
        try {
            httpClient.close();
            httpAsyncClient.close();
        } catch (IOException ignored) { }
        connectionManager.close();
    }

    @Override
    public SpiderResponse downloadSync(SpiderRequest request) {
        HttpRequestBase httpRequest;
        try {
            URL url = normalizeUrl(request);
            httpRequest = buildRequest(request);
            setHeader(request, httpRequest);
            contexts.computeIfAbsent(url.getHost(), this::buildContext);

            HttpClientContext context = contexts.get(url.getHost());
            setCookie(request, context);

            try (CloseableHttpResponse httpResponse = httpClient.execute(httpRequest, context)) {
                return buildResponse(httpResponse, request);
            } catch (Exception e) {
                return buildResponse(e, request);
            } finally {
                httpRequest.releaseConnection();
            }
        } catch (SpiderException e) {
            log.error(e.getMessage(), e);
            return SpiderResponse.fail(request);
        }
    }

    @Override
    public CompletableFuture<SpiderResponse> downloadAsync(SpiderRequest request) {
        CompletableFuture<SpiderResponse> future = new CompletableFuture<>();
        HttpRequestBase httpRequest;
        try {
            URL url = normalizeUrl(request);
            httpRequest = buildRequest(request);
            setHeader(request, httpRequest);
            contexts.computeIfAbsent(url.getHost(), this::buildContext);

            HttpClientContext context = contexts.get(url.getHost());
            setCookie(request, context);

            httpAsyncClient.execute(httpRequest, context, new Callback(request, future));
        } catch (SpiderException e) {
            future.complete(SpiderResponse.fail(request));
        }
        return future;
    }

    private HttpClientContext buildContext(String host) {
        HttpClientContext context = new HttpClientContext();
        BasicCookieStore cookieStore = new BasicCookieStore();
        context.setCookieStore(cookieStore);
        return context;
    }

    private HttpRequestBase buildRequest(SpiderRequest request) throws SpiderRuntimeException {
        switch (request.getMethod()) {
            case GET:
                return buildGetRequest(request);
            case POST:
                return buildPostRequest(request);
            default:
                throw new SpiderRuntimeException("unsupported request method: " + request.getMethod());
        }
    }

    private HttpGet buildGetRequest(SpiderRequest request) {
        return new HttpGet(request.getUrl());
    }

    private HttpPost buildPostRequest(SpiderRequest request) {
        HttpPost post = new HttpPost(request.getUrl());
        BasicHttpEntity entity = new BasicHttpEntity();
        entity.setContent(new ByteArrayInputStream(request.getBody()));
        post.setEntity(entity);
        return post;
    }

    private void setHeader(SpiderRequest request, HttpRequest httpRequest) {
        if (MapUtils.isNotEmpty(request.getHeaders())) {
            request.getHeaders().forEach(httpRequest::setHeader);
        }
    }

    private void setCookie(SpiderRequest request, HttpClientContext context) {
        if (MapUtils.isNotEmpty(request.getCookies())) {
            request.getCookies()
                    .forEach((key, value) -> context.getCookieStore().addCookie(new BasicClientCookie(key, value)));
        }
    }

    private SpiderResponse buildResponse(HttpResponse httpResponse, SpiderRequest request) throws IOException {
        return SpiderResponse.builder()
                .url(request.getUrl())
                .headers(Arrays.stream(httpResponse.getAllHeaders())
                        .collect(Collectors.toMap(Header::getName, Header::getValue, (a, b) -> a)))
                .status(httpResponse.getStatusLine().getStatusCode())
                .body(readBody(httpResponse))
                .charset(getCharset(httpResponse))
                .request(request)
                .build();
    }

    private SpiderResponse buildResponse(Exception e, SpiderRequest request) {
        log.error("Failed to download: {}", request.getUrl(), e);
        return SpiderResponse.fail(request);
    }

    private byte[] readBody(HttpResponse response) throws IOException {
        return EntityUtils.toByteArray(response.getEntity());
    }

    private Charset getCharset(HttpResponse response) {
        ContentType contentType = ContentType.getOrDefault(response.getEntity());
        return Objects.nonNull(contentType.getCharset()) ? contentType.getCharset() : SpiderConstant.DEFAULT_CHARSET;
    }

    private URL normalizeUrl(SpiderRequest request) throws SpiderException {
        URL url;
        if (!StringUtils.startsWith(request.getUrl(), "http")) {
            request.setUrl("http://" + request.getUrl());
        }
        url = checkUrl(request.getUrl());
        if (Objects.nonNull(url)) {
            return url;
        }
        throw new SpiderException("malformed url: " + request.getUrl());
    }

    private URL checkUrl(String url) {
        try {
            return new URL(url);
        } catch (MalformedURLException e) {
            return null;
        }
    }

    private class Callback implements FutureCallback<HttpResponse> {
        private final SpiderRequest request;
        private final CompletableFuture<SpiderResponse> future;

        private Callback(SpiderRequest request, CompletableFuture<SpiderResponse> future) {
            this.request = request;
            this.future = future;
        }

        @Override
        public void completed(HttpResponse result) {
           try {
               future.complete(buildResponse(result, request));
            } catch (IOException e) {
                future.complete(SpiderResponse.fail(request));
            }
        }

        @Override
        public void failed(Exception ex) {
            future.complete(buildResponse(ex, request));
        }

        @Override
        public void cancelled() {
            future.complete(buildResponse(new Exception("cancelled"), request));
        }
    }
}
