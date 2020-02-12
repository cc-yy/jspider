package me.jspider.core.pipeline.filter;

import me.jspider.base.bean.SpiderDataItem;
import me.jspider.core.pipeline.Pipeline;
import me.jspider.core.pipeline.PipelineChain;
import me.jspider.core.setting.Setting;
import org.apache.commons.lang3.StringUtils;

/**
 * A {@link Pipeline} which drops items by evaluating an expression, see {@link Setting#getPipelineFilterExpression()}.
 * The evaluations and judgements varies among implementations.
 */
public abstract class BaseItemFilter implements Pipeline {
    protected static final String ITEM = "item";
    protected static final String TRUE = String.valueOf(true);

    protected boolean active;
    protected String expr;

    @Override
    public void process(SpiderDataItem item, PipelineChain chain) {
        if (active && drop(item)) {
            return;
        }
        chain.process(item);
    }

    @Override
    public void open(Setting setting) {
        if (StringUtils.isNoneBlank(setting.getPipelineFilterExpression())) {
            expr = setting.getPipelineFilterExpression();
            active = true;
        }
    }

    @Override
    public void close() { }

    protected abstract boolean drop(SpiderDataItem item);
}
