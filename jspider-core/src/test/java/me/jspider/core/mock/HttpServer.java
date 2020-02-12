package me.jspider.core.mock;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import me.jspider.base.common.SpiderTestConstant;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;

public class HttpServer {
    private final WireMockServer server;

    public HttpServer(int port) {
        server = new WireMockServer(WireMockConfiguration.wireMockConfig().port(port));
    }

    public void start() {
        server.addStubMapping(WireMock.get(SpiderTestConstant.HTTP_OK_PATH)
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.SC_OK)
                        .withHeader(HttpHeaders.CONTENT_TYPE, "text/html")
                        .withBody("<!DOCTYPE html><html><head><title>hello</title></head></html>"))
                .build());
        server.addStubMapping(WireMock.get(SpiderTestConstant.HTTP_TEMPORARILY_MOVED_PATH)
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.SC_MOVED_TEMPORARILY)
                        .withHeader(HttpHeaders.LOCATION, SpiderTestConstant.HTTP_OK_PATH))
                .build());
        server.addStubMapping(WireMock.get(SpiderTestConstant.HTTP_NOT_FOUND_PATH)
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.SC_NOT_FOUND))
                .build());
        server.start();
    }

    public void stop() {
        server.stop();
    }

    public static void main(String[] args) {
        new HttpServer(SpiderTestConstant.HTTP_PORT).start();
    }
}
