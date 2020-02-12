package me.jspider.core.middleware;

import me.jspider.base.bean.SpiderRequest;
import me.jspider.base.bean.SpiderResponse;
import me.jspider.base.common.SpiderConstant;
import me.jspider.core.setting.Setting;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * Normalizes the request and filters out illegal ones.
 */
@Slf4j
public class RequestNormalizer implements Middleware {
    @Override
    public boolean processRequest(SpiderRequest request) {
        return checkRequest(request);
    }

    @Override
    public boolean processResponse(SpiderResponse response) {
        return true;
    }

    @Override
    public void open(Setting setting) { }

    @Override
    public void close() { }

    private boolean checkRequest(SpiderRequest request) {
        try {
            if (StringUtils.isEmpty(request.getUrl())) {
                log.warn("Drop request: empty url.");
                return false;
            }
            if (!SpiderConstant.SUPPORTED_HTTP_METHOD.contains(request.getMethod())) {
                log.warn("Drop request: illegal http method: {}.", request.getMethod());
                return false;
            }
        } catch (Exception e) {
            log.warn("Drop request: something wrong. request={}", request, e);
            return false;
        }
        return true;
    }
}
