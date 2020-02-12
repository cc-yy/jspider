package me.jspider.core.middleware;

import me.jspider.base.bean.SpiderRequest;
import me.jspider.base.bean.SpiderResponse;
import me.jspider.core.base.OpenClosable;

/**
 * A spider component which processes the outgoing request and incoming response.
 */
public interface Middleware extends OpenClosable {
    /**
     * Process the outgoing request.
     *
     * @param request
     * @return {@code true} for further processing, false to terminate
     */
    boolean processRequest(SpiderRequest request);

    /**
     * Process the incoming response.
     *
     * @param response
     * @return {@code true} for further processing, false to terminate
     */
    boolean processResponse(SpiderResponse response);
}
