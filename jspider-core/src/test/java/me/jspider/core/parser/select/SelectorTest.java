package me.jspider.core.parser.select;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class SelectorTest {
    private static JsonDocument jsonDocument;
    private static HtmlDocument htmlDocument;

    @BeforeAll
    public static void setup() {
        jsonDocument = JsonDocument.build("{\"id\":1,\"result\":[{\"name\":\"a\",\"data\":[1,2,3]},{\"name\":\"b\",\"data\":[2]},{\"name\":\"c\",\"data\":null}]}");
        htmlDocument = HtmlDocument.build("<!DOCTYPE html><html><body><div id=\"content\"><ul><li><a href=\"abc\"></a>a</li><li><a href=\"def\">d</a></li></ul></div></body></html>");
    }

    @Test
    public void test_Json() {
        Selector selector = Selector.json("$.result[*].name");
        Assertions.assertEquals(3, selector.select(jsonDocument).size());
        Assertions.assertEquals("a", selector.select(jsonDocument).first().text());
    }

    @Test
    public void test_Xpath() {
        Selector selector = Selector.xpath("//");
        Assertions.assertThrows(UnsupportedOperationException.class, () -> selector.select(htmlDocument));
    }

    @Test
    public void test_Css() {
        Selector selector = Selector.css("#content li");
        Assertions.assertEquals(2, selector.select(htmlDocument).size());

        selector = Selector.css("#content li:nth-last-child(1) > a::text()");
        Assertions.assertEquals("d", selector.select(htmlDocument).first().text());


        selector = Selector.css("#content li:nth-child(1) > a::attr(href)");
        Assertions.assertEquals("abc", selector.select(htmlDocument).first().text());

        selector = Selector.css("#content li > a::attr(href)");
        Assertions.assertEquals("abc,def", selector.select(htmlDocument).text());
    }
}
