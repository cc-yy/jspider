package me.jspider.core.parser.select;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class JsonDocumentTest {

    private static JsonDocument document;

    @BeforeAll
    public static void setup() {
        document = JsonDocument.build("{\"id\":1,\"result\":[{\"name\":\"a\",\"data\":[1,2,3]},{\"name\":\"b\",\"data\":[2]},{\"name\":\"c\",\"data\":null}]}");
    }

    @Test
    public void testJson_Normal() {
        Assertions.assertEquals(3, document.json("$.result").size());
        Assertions.assertEquals(1, document.json("$.result[0].data[0]").first().repr());
        Assertions.assertNull(document.json("$.result[2].data").first().repr());
        Assertions.assertEquals(0, document.json("$.result[10]").size());
    }

    @Test
    public void testJson_Blank() {
        Assertions.assertEquals(document, document.json("").first());
    }

    @Test
    public void test_Ex() {
        Assertions.assertThrows(UnsupportedOperationException.class, () -> document.xpath(""));
        Assertions.assertThrows(UnsupportedOperationException.class, () -> document.css(""));
    }
}
