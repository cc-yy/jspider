package me.jspider.base.util;

import me.jspider.base.bean.SpiderRequest;
import me.jspider.common.util.SpiderUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SpiderUtilsTest {

    @Test
    public void testSignRequest() {
        SpiderRequest request = SpiderRequest.builder()
                .url("www.baidu.com")
                .build();
        Assertions.assertEquals("d2a9fdc09ab6f53f45714be4035aca02", SpiderUtils.signRequest(request));

        request.setBody(request.getUrl().getBytes());
        Assertions.assertEquals("cfb32e04a9a8188fa00546c7bfa80b9b", SpiderUtils.signRequest(request));
    }
}
