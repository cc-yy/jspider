package me.jspider.core.parser.select;

/**
 * A selectable against which selection can be performed.
 * Json and css/xpath are mixed for simplicity, but they should never be used in mixture.
 */
public interface Selectable {
    /**
     * Via css selector.
     */
    Selection css(String selector);

    /**
     * Via xpath.
     */
    Selection xpath(String xpath);

    /**
     * Via json path.
     */
    Selection json(String json);

    /**
     * An appropriate representation of this selectable, depending implementations.
     */
    Object repr();

    /**
     * Text representation.
     */
    String text();
}
