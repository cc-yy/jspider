package me.jspider.base.parser;

import me.jspider.base.bean.SpiderDataItem;
import me.jspider.base.bean.SpiderItem;
import me.jspider.base.bean.SpiderResponse;
import me.jspider.base.bean.SpiderRequest;

import java.util.List;

/**
 * A parser which extracts data from response.
 */
public interface SpiderParser {
    /**
     * Parse the response and extract data.
     * @return A iterable instance holding {@link SpiderRequest}s and {@link SpiderDataItem}s.
     */
    List<SpiderItem> parse(SpiderResponse response);
}
