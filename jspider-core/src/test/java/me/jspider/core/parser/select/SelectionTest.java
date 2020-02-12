package me.jspider.core.parser.select;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SelectionTest {
    @Test
    public void testCss() {
        Selection selection = new Selection();
        selection.add(HtmlDocument.build("<!DOCTYPE html><html><body><div class=\"content\">1</div></body></html>"));
        selection.add(HtmlDocument.build("<!DOCTYPE html><html><body><div class=\"content\">2</div></body></html>"));

        Selection result = selection.css("div.content");
        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals("1,2", result.text());
    }

    @Test
    public void testJson() {
        Selection selection = new Selection();
        selection.add(JsonDocument.build("{\"a\":1,\"b\":2}"));
        selection.add(JsonDocument.build("{\"a\":10,\"b\":20}"));

        Selection result = selection.json("$.a");
        Assertions.assertEquals(2, selection.size());
        Assertions.assertEquals("1,10", result.text());
    }
}
