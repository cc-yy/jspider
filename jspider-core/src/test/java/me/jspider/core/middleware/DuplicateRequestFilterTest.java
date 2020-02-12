package me.jspider.core.middleware;

import me.jspider.base.bean.SpiderRequest;
import me.jspider.core.setting.Setting;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DuplicateRequestFilterTest {
    private DuplicateRequestFilter filter;

    @BeforeEach
    public void setup() {
        filter = new DuplicateRequestFilter();
        filter.open(new Setting());
    }

    @Test
    public void testProcessRequest_Filter() {
        Assertions.assertTrue(filter.processRequest(SpiderRequest.builder().url("a").build()));
        Assertions.assertFalse(filter.processRequest(SpiderRequest.builder().url("a").build()));
        Assertions.assertTrue(filter.processRequest(SpiderRequest.builder().url("b").build()));
        Assertions.assertFalse(filter.processRequest(SpiderRequest.builder().url("a").build()));
    }

}
