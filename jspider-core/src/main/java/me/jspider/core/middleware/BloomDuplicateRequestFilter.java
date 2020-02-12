package me.jspider.core.middleware;

import me.jspider.base.bean.SpiderRequest;
import me.jspider.base.bean.SpiderResponse;
import me.jspider.common.util.SpiderUtils;
import me.jspider.core.setting.Setting;
import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Filters duplicate requests based on bloom filter.
 */
@Component
@Slf4j
public class BloomDuplicateRequestFilter implements Middleware {
    private BloomFilter<SpiderRequest> filter;

    @Override
    public boolean processRequest(SpiderRequest request) {
        if (filter.mightContain(request)) {
            log.info("Filter duplicate request: {} {}", request.getMethod(), request.getUrl());
            return false;
        }
        filter.put(request);
        return true;
    }

    @Override
    public boolean processResponse(SpiderResponse response) {
        return true;
    }

    @Override
    public void open(Setting setting) {
        filter = BloomFilter.create((Funnel<SpiderRequest>) (from, into) -> {
            String sign = SpiderUtils.signRequest(from);
            into.putBytes(sign.getBytes());
        }, 100000);
    }

    @Override
    public void close() { }
}
