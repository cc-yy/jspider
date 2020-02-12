package me.jspider.common.util;

import me.jspider.base.bean.SpiderRequest;
import me.jspider.base.bean.SpiderRuntimeException;
import com.google.common.base.Preconditions;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.ArrayUtils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SpiderUtils {
    private SpiderUtils() { }

    public static String signRequest(SpiderRequest request) {
        Preconditions.checkNotNull(request);

        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(request.getMethod().getBytes());
            digest.update(request.getUrl().getBytes(StandardCharsets.UTF_8));
            if (ArrayUtils.isNotEmpty(request.getBody())) {
                digest.update(request.getBody());
            }
            digest.update((byte) request.getRetry());
            byte[] b = digest.digest();
            return Hex.encodeHexString(b);
        } catch (NoSuchAlgorithmException e) {
            throw new SpiderRuntimeException(e);
        }
    }
}
