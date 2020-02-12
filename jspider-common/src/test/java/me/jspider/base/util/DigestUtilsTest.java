package me.jspider.base.util;

import me.jspider.common.util.DigestUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DigestUtilsTest {
    @Test
    public void testHmacSHA512() {
        String key = "abc";
        String message = "123";
        Assertions.assertEquals("1bb47a2e086bfab3a86e3843ffd665fead90f0ef46cf2894c56a194fb18158685e9fd364bde008d5f2cb04e649c7396adda38dc5617a9dd56ab981920ae13188",
                DigestUtils.hmacSha512(message, key));
    }
}
