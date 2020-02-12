package me.jspider.core.pipeline.filter;

import me.jspider.base.bean.SpiderDataItem;
import me.jspider.core.setting.Setting;
import lombok.Getter;
import lombok.ToString;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class FreemarkerItemFilterTest {
    private FreemarkerItemFilter filter;
    Setting setting;

    @BeforeEach
    public void setup() {
        filter = new FreemarkerItemFilter();
        setting = new Setting();
    }

    @Test
    public void testDrop_Constant() {
        setting.setPipelineFilterExpression("1");
        filter.open(setting);

        Assertions.assertFalse(filter.drop(new TestItem(1, 0, "1")));
    }

    @Test
    public void testDrop_Expr() {
        setting.setPipelineFilterExpression("<#if i &lt; 0>true</#if>");
        filter.open(setting);

        Assertions.assertTrue(filter.drop(new TestItem(1, 0, "")));
        Assertions.assertFalse(filter.drop(new TestItem(-1, 0, "")));

    }

    @Getter
    @ToString
    public static class TestItem implements SpiderDataItem {
        public int i;
        public double d;
        public String s;

        TestItem(int i, double d, String s) {
            this.i = i;
            this.d = d;
            this.s = s;
        }
    }
}
