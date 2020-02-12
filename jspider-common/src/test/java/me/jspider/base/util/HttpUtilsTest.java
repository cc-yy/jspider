package me.jspider.base.util;

import me.jspider.common.util.HttpUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class HttpUtilsTest {
    @Test
    public void testEncodeUrlParam() {

        Assertions.assertEquals("%E4%BD%A0%E5%A5%BD", HttpUtils.encodeUrlParam("你好"));
    }
}
