package me.jspider.common.util;

import me.jspider.base.bean.SpiderRuntimeException;
import me.jspider.base.common.SpiderConstant;
import org.apache.commons.codec.binary.Hex;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class DigestUtils {
    private DigestUtils() { }

    public static String hmacSha512(String message, String key) {
        byte[] bytesKey = key.getBytes();
        final SecretKeySpec secretKey = new SecretKeySpec(bytesKey, "HmacSHA512");
        try {
            Mac mac = Mac.getInstance("HmacSHA512");
            mac.init(secretKey);
            final byte[] macData = mac.doFinal(message.getBytes());
            byte[] hex = new Hex().encode(macData);
            return new String(hex, SpiderConstant.DEFAULT_CHARSET);
        } catch (Exception e) {
            throw new SpiderRuntimeException("Failed to calculate HmacSHA512");
        }
    }
}
