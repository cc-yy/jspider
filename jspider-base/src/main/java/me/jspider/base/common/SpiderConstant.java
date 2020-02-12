package me.jspider.base.common;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Holds constants.
 */
public class SpiderConstant {
    public static final String BASE_PACKAGE_NAME = "me.spider";

    public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    public static final String DEFAULT_ENCODING = DEFAULT_CHARSET.displayName();

    public static final ImmutableList<String> SUPPORTED_HTTP_METHOD = ImmutableList.of("GET", "POST");

    public static final ImmutableMap<String, String> PC_HEADER = ImmutableMap.<String, String>builder()
            .put("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/77.0.3865.90 Safari/537.36")
            .put("Accept", "application/json,text/html")
            .put("Accept-Charset", "utf-8")
            .build();

    public static final ImmutableMap<String, String> MOBILE_HEADER = ImmutableMap.<String, String>builder()
            .put("User-Agent", "Mozilla/5.0 (Linux; U; Android 4.2; zh-cn; Nexus One Build/FRF91) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1")
            .put("Accept", "application/json,text/html")
            .put("Accept-Charset", "utf-8")
            .build();

    private SpiderConstant() { }
}
