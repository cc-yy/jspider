package me.jspider.core.parser.select;

import lombok.EqualsAndHashCode;
import me.jspider.base.bean.SpiderRuntimeException;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Implementations:
 *  <li>css selector: <a href="https://jsoup.org/">jsoup</a></li>.
 */
@EqualsAndHashCode
public class HtmlDocument implements Selectable {
    private Element element;

    public static HtmlDocument build(Object object) {
        if (object instanceof String) {
            return new HtmlDocument(Jsoup.parse((String) object));
        } else if (object instanceof Element) {
            return new HtmlDocument((Element) object);
        } else {
            throw new SpiderRuntimeException(object.getClass().getName() + "is not supported in HtmlDocument");
        }
    }

    public static HtmlDocument build(String html, String baseUrl) {
        return new HtmlDocument(Jsoup.parse(html, baseUrl));
    }

    private HtmlDocument(Element element) {
        this.element = element;
    }

    @Override
    public Selection css(String selector) {
        Selection selection = new Selection();

        if (StringUtils.isEmpty(selector)) {
            selection.add(this);
            return selection;
        }

        Elements elements = element.select(selector);
        elements.stream().map(HtmlDocument::build).forEach(selection::add);
        return selection;
    }

    @Override
    public Selection xpath(String xpath) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Selection json(String json) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object repr() {
        return element.html();
    }

    @Override
    public String text() {
        return element.ownText();
    }

    public String attr(String key) {
        return element.attr(key);
    }

}
