package me.jspider.core.parser.select;

import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;

/**
 * A selectable representing {@code null}.
 */
@EqualsAndHashCode
public class Null implements Selectable {
    private static Null instance = new Null();

    public static Null build() {
        return instance;
    }

    private Null() { }

    @Override
    public Selection css(String selector) {
        return select();
    }

    @Override
    public Selection xpath(String xpath) {
        return select();
    }

    @Override
    public Selection json(String json) {
        return select();
    }

    @Override
    public Object repr() {
        return null;
    }

    @Override
    public String text() {
        return StringUtils.EMPTY;
    }

    private Selection select() {
        Selection selection = new Selection();
        selection.add(this);
        return selection;
    }
}
