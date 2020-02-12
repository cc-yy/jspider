package me.jspider.core.parser.loader;

import com.google.common.collect.Lists;
import me.jspider.base.bean.SpiderItem;
import me.jspider.base.bean.SpiderRequest;
import me.jspider.base.bean.SpiderResponse;
import me.jspider.base.bean.SpiderRuntimeException;
import me.jspider.base.parser.SpiderParser;
import me.jspider.core.parser.select.HtmlDocument;
import me.jspider.core.parser.select.JsonDocument;
import me.jspider.core.parser.select.Selectable;
import me.jspider.core.parser.select.Selector;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public abstract class SpiderItemLoader implements SpiderParser {

    @Override
    public List<SpiderItem> parse(SpiderResponse response) {
        preProcess(response);

        if (StringUtils.isEmpty(response.getText())) {
            return Collections.emptyList();
        }

        List<SpiderItem> results = Lists.newArrayList();
        Selector selector = itemListSelector();
        Selectable document = buildDocument(response, selector);

        List<Selectable> list = selector.select(document);
        ItemMapper mapper = itemMapper();
        List<SpiderItem> items = Lists.newArrayList();
        for (Selectable o : list) {
            SpiderItem item = mapper.apply(response, o);
            if (Objects.nonNull(item)) {
                items.add(item);
            }
        }

        postProcess(response, items);
        results.addAll(items);

        SpiderRequest request = nextPage(response, document);
        if (Objects.nonNull(request)) {
            results.add(request);
        }

        return results;
    }

    /**
     * Called before extraction.
     */
    protected void preProcess(SpiderResponse response) { }

    /**
     * Called after items extracted.
     */
    protected void postProcess(SpiderResponse response, List<? extends SpiderItem> items) { }

    /**
     * A selector that extracts item list from document.
     */
    protected abstract Selector itemListSelector();

    /**
     * A mapper maps a selectable from document to spider item.
     * @return
     */
    protected abstract ItemMapper itemMapper();

    /**
     * Build a request to get next page.
     */
    protected SpiderRequest nextPage(SpiderResponse response, Selectable document) {
        return null;
    }

    private Selectable buildDocument(SpiderResponse response, Selector selector) {
        if (selector instanceof Selector.JsonSelector) {
            return JsonDocument.build(response.getText());
        } else if (selector instanceof Selector.XpathSelector || selector instanceof Selector.CssSelector) {
            return HtmlDocument.build(response.getText(), response.getUrl());
        } else {
            throw new SpiderRuntimeException("unknown selector: " + selector.getClass().getSimpleName());
        }
    }

    public interface ItemMapper {
        SpiderItem apply(SpiderResponse response, Selectable selectable);
    }
}
