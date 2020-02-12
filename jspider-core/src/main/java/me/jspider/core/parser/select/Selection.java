package me.jspider.core.parser.select;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

/**
 * A collection of selected objects.
 */
public class Selection extends ArrayList<Selectable> implements Selectable {

    public Selection() { }

    public Selection(List<Selectable> list) {
        super(list);
    }

    @Override
    public Selection css(String selector) {
        return select(selector, Selectable::css);
    }

    @Override
    public Selection xpath(String xpath) {
        return select(xpath, Selectable::xpath);
    }

    @Override
    public Selection json(String json) {
        return select(json, Selectable::json);
    }

    @Override
    public Object repr() {
        return this;
    }

    @Override
    public String text() {
        return Joiner.on(',').join(this.stream().map(Selectable::text).iterator());
    }

    public Selectable first() {
        return this.isEmpty() ? null : this.get(0);
    }

    private Selection select(String query, BiFunction<Selectable, String, Selection> function) {
        List<Selectable> list = Lists.newArrayListWithCapacity(this.size());
        this.forEach(s -> list.addAll(function.apply(s, query)));
        return new Selection(list);
    }
}
