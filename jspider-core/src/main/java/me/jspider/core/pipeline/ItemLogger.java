package me.jspider.core.pipeline;

import com.alibaba.fastjson.JSON;
import me.jspider.base.bean.SpiderDataItem;
import me.jspider.core.setting.Setting;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Logs the items going through.
 */
@Slf4j
@Order
@Component
public class ItemLogger implements Pipeline {
    @Override
    public void process(SpiderDataItem item, PipelineChain chain) {
        try {
            log.info("Got item: {}.", JSON.toJSONString(item));
            chain.process(item);
        } catch (Exception ignored) {}
    }

    @Override
    public void open(Setting setting) { }

    @Override
    public void close() { }
}
