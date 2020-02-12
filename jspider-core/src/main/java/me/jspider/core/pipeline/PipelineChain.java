package me.jspider.core.pipeline;

import me.jspider.base.bean.SpiderDataItem;
import me.jspider.core.setting.Setting;
import me.jspider.core.base.OrderedChain;

import java.util.Iterator;
import java.util.List;

/**
 * A chain holds {@link Pipeline}s.
 */
public class PipelineChain implements Pipeline {
    private final Iterator<Pipeline> iterator;

    public PipelineChain(List<Pipeline> pipelines) {
        OrderedChain<Pipeline> chain = new OrderedChain<>();
        pipelines.forEach(chain::add);
        iterator = chain.iterator();
    }

    public PipelineChain(OrderedChain<Pipeline> chain) {
        OrderedChain<Pipeline> c = new OrderedChain<>();
        chain.forEach(c::add);
        iterator = c.iterator();
    }

    public void process(SpiderDataItem item) {
        if (iterator.hasNext()) {
            iterator.next().process(item, this);
        }
    }

    @Override
    public void process(SpiderDataItem item, PipelineChain chain) {
        this.process(item);
    }

    @Override
    public void open(Setting setting) { }

    @Override
    public void close() { }
}
