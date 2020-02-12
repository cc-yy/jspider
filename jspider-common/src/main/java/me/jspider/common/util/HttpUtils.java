package me.jspider.common.util;

import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpUtils {
    private static final String CHARSET = "charset=";

    private HttpUtils() {
    }

    public static String encodeUrlParam(String s) {
        try {
            return URLEncoder.encode(s, StandardCharsets.UTF_8.displayName());
        } catch (UnsupportedEncodingException e) {
            return StringUtils.EMPTY;
        }
    }

    public static String encodeUrl(String url) {
        try {
            return URLEncoder.encode(url, StandardCharsets.UTF_8.displayName());
        } catch (UnsupportedEncodingException e) {
            return StringUtils.EMPTY;
        }
    }

    public static String compactHeader(Map<String, String> header) {
        if (MapUtils.isEmpty(header)) {
            return StringUtils.EMPTY;
        }
        return Joiner.on("\r\n")
                .join(header.entrySet().stream().map(en -> en.getKey() + ": " + en.getValue()).collect(Collectors.toList()));
    }

    public static Map<String, String> extractHeader(String header) {
        if (StringUtils.isBlank(header)) {
            return Collections.emptyMap();
        }
        Map<String, String> map = Maps.newHashMap();
        for (String h : StringUtils.split(header, "\r\n")) {
            map.put(StringUtils.substringBefore(h, ":"), StringUtils.substringAfter(h, ":"));
        }
        return map;
    }

    public static String compactCookie(Map<String, String> cookie) {
        if (MapUtils.isEmpty(cookie)) {
            return StringUtils.EMPTY;
        }
        return Joiner.on("; ")
                .join(cookie.entrySet().stream().map(en -> en.getKey() + "=" + en.getValue()).collect(Collectors.toList()));
    }

    public static Map<String, String> extractCookie(String cookie) {
        if (StringUtils.isBlank(cookie)) {
            return Collections.emptyMap();
        }
        Map<String, String> map = Maps.newHashMap();
        for (String h : StringUtils.split(cookie, ";")) {
            map.put(StringUtils.substringBefore(h, "="), StringUtils.substringAfter(h, "="));
        }
        return map;
    }
}
