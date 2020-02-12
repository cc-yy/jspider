package me.jspider.common.util;

import me.jspider.base.bean.SpiderRuntimeException;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

public class UrlUtils {

    public static String join(String base, String... urls) {
        URL baseUrl = null;
        try {
            baseUrl = new URL(base);
            for (String u : urls) {
                baseUrl = new URL(baseUrl, u);
            }
            return baseUrl.toString();
        } catch (MalformedURLException e) {
            throw new SpiderRuntimeException("broken url: " + base + ";" + Arrays.toString(urls));
        }
    }
}
