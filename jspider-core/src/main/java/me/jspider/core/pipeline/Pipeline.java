package me.jspider.core.pipeline;

import me.jspider.base.bean.SpiderDataItem;
import me.jspider.core.base.OpenClosable;

/**
 * A spider component which processes {@link SpiderDataItem}s going through it.
 */
public interface Pipeline extends OpenClosable {

    void process(SpiderDataItem item, PipelineChain chain);
}
