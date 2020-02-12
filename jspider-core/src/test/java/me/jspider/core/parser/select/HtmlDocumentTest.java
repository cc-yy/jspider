package me.jspider.core.parser.select;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class HtmlDocumentTest {
    private static HtmlDocument document;

    @BeforeAll
    public static void setup() {
        document = HtmlDocument.build("<!DOCTYPE html><html><body><div id=\"content\"><ul><li><a href=\"abc\"></a>a</li><li><a href=\"def\">d</a></li></ul></div></body></html>");
    }


    @Test
    public void testCss_Normal() {
        Assertions.assertEquals(1, document.css("#content").size());
        Assertions.assertEquals(2, document.css("#content li").size());
        Assertions.assertEquals("a", document.css("#content li:nth-child(1)").text());
    }

    @Test
    public void testCss_Blank() {
        Assertions.assertEquals(document, document.css("").first());
    }

    @Test
    public void test_Ex() {
        Assertions.assertThrows(UnsupportedOperationException.class, () -> document.json(""));
        Assertions.assertThrows(UnsupportedOperationException.class, () -> document.xpath(""));
    }
}
