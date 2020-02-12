package me.jspider.core.parser.select;

import com.google.common.collect.ImmutableSet;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface Selector {
    Selection select(Selectable selectable);

    static JsonSelector json(String json) {
        return new JsonSelector(json);
    }

    static XpathSelector xpath(String xpath) {
        return new XpathSelector(xpath);
    }

    static CssSelector css(String css) {
        return new CssSelector(css);
    }

    /**
     * A json-path selector.
     */
    class JsonSelector implements Selector {
        private final String json;

        public JsonSelector(String json) {
            this.json = json;
        }

        @Override
        public Selection select(Selectable selectable) {
            return selectable.json(json);
        }

        @Override
        public String toString() {
            return json;
        }
    }

    /**
     * An xpath selector.
     */
    class XpathSelector implements Selector {
        private final String xpath;

        public XpathSelector(String xpath) {
            this.xpath = xpath;
        }

        @Override
        public Selection select(Selectable selectable) {
            return selectable.xpath(xpath);
        }

        @Override
        public String toString() {
            return xpath;
        }
    }

    /**
     * An css selector, one extended version that also supports {@code ::text()} and {@code ::attr(attr-name)} for convenience.
     */
    class CssSelector implements Selector {
        private static final Pattern PSEUDO_ELEMENT_PATTERN = Pattern.compile("::(\\w+)\\((\\S*)\\)$");
        private static final Set<String> EXTRA_PSEUDO_ELEMENT = ImmutableSet.of("attr", "text");
        private final String css;
        private Pair<String, String> extra = null;

        public CssSelector(String css) {
            Matcher matcher = PSEUDO_ELEMENT_PATTERN.matcher(css.trim());
            if (matcher.find()) {
                String e = matcher.group(1);
                if (EXTRA_PSEUDO_ELEMENT.contains(e)) {
                    extra = ImmutablePair.of(e, matcher.group(2));
                    css = matcher.replaceAll("");
                }
            }
            this.css = css;
        }

        @Override
        public Selection select(Selectable selectable) {
            Selection selection = selectable.css(css);
            if (Objects.nonNull(extra)) {
                for (int i = 0; i < selection.size(); ++i) {
                    Selectable s = selection.get(i);
                    if (!(s instanceof HtmlDocument)) {
                        continue;
                    }
                    HtmlDocument document = (HtmlDocument) s;
                    selection.set(i, extendedCss(document));
                }
            }
            return selection;
        }

        @Override
        public String toString() {
            return css;
        }

        private Text extendedCss(HtmlDocument document) {
            switch (extra.getLeft()) {
                case "attr":
                    return Text.build(document.attr(extra.getRight()));
                case "text":
                    return Text.build(document.text());
            }
            return Text.build(StringUtils.EMPTY);
        }
    }
}
