package me.jspider.core.scheduler;

import me.jspider.base.bean.SpiderDataItem;
import me.jspider.base.bean.SpiderRequest;
import me.jspider.base.bean.SpiderResponse;
import me.jspider.core.base.StartStoppable;

/**
 * A spider component which schedules the control flow (request, response and items).
 */
public interface Scheduler extends StartStoppable {

    /**
     * Offers one request to the scheduler.
     */
    void offerRequest(SpiderRequest request);

    /**
     * Ask for a request and remove it.
     */
    SpiderRequest takeRequest();

    /**
     * Offers one response to the scheduler.
     */
    void offerResponse(SpiderResponse response);

    /**
     * Ask for a response and remove it.
     */
    SpiderResponse takeResponse();


    /**
     * Offers one item to the scheduler.
     */
    void offerItem(SpiderDataItem item);

    /**
     * Ask for an item and remove it.
     */
    SpiderDataItem takeItem();
}
