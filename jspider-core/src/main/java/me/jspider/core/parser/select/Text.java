package me.jspider.core.parser.select;

import lombok.EqualsAndHashCode;

/**
 * A plain text implementation of {@code Selectable}.
 */
@EqualsAndHashCode
public final class Text implements Selectable {
    private final String text;

    public static Text build(String text) {
        return new Text(text);
    }

    private Text(String text) {
        this.text = text;
    }

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
        return text;
    }

    @Override
    public String text() {
        return text;
    }

    private Selection select() {
        Selection selection = new Selection();
        selection.add(this);
        return selection;
    }
}
