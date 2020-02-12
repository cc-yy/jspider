package me.jspider.core.engine.monitor;

import me.jspider.core.base.StartStoppable;
import me.jspider.base.bean.SpiderDataItem;

/**
 * An engine component which monitors the events in the engine.
 */
public interface Monitor extends StartStoppable {
    /**
     * When a request comes.
     */
    void onRequest();

    /**
     * When a request is successful processed.
     */
    void onSuccess();

    /**
     * When a request failed after max retries.
     */
    void onFailure();

    /**
     * When an {@link SpiderDataItem} comes.
     */
    void onItem();
}
