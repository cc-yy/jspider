package me.jspider.core.middleware;

import me.jspider.base.bean.SpiderRequest;
import me.jspider.base.bean.SpiderResponse;
import me.jspider.common.util.SpiderUtils;
import me.jspider.core.setting.Setting;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.concurrent.ConcurrentMap;

/**
 * Filters duplicate requests.
 */
@Component
@Slf4j
public class DuplicateRequestFilter implements Middleware {
    private ConcurrentMap<String, String> signatures;

    @Override
    public boolean processRequest(SpiderRequest request) {
        String sign = SpiderUtils.signRequest(request);
        if (Objects.nonNull(signatures.putIfAbsent(sign, StringUtils.EMPTY))) {
            log.info("Filter duplicate request: {} {}", request.getMethod(), request.getUrl());
            return false;
        }
        signatures.put(sign, StringUtils.EMPTY);
        return true;
    }

    @Override
    public boolean processResponse(SpiderResponse response) {
        return true;
    }

    @Override
    public void open(Setting setting) {
        signatures = Maps.newConcurrentMap();
    }

    @Override
    public void close() { }
}
