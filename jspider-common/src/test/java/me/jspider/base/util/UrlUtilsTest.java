package me.jspider.base.util;

import me.jspider.common.util.UrlUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class UrlUtilsTest {
    @Test
    public void testJoin() {
        Assertions.assertEquals("http://baidu.com", UrlUtils.join("http://baidu.com"));
        Assertions.assertEquals("http://baidu.com/1.html", UrlUtils.join("http://baidu.com", "1.html"));
        Assertions.assertEquals("http://baidu.com/2.html", UrlUtils.join("http://baidu.com", "1.html", "2.html"));
        Assertions.assertEquals("http://baidu.com/1.html?abc", UrlUtils.join("http://baidu.com", "1.html?abc"));
    }
}
