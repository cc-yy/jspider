package me.jspider.core.pipeline.filter;

import me.jspider.base.bean.SpiderDataItem;
import me.jspider.core.setting.Setting;
import lombok.Getter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SpelItemFilterTest {
    private SpelItemFilter filter;
    Setting setting;

    @BeforeEach
    public void setup() {
        filter = new SpelItemFilter();
        setting = new Setting();
    }

    @Test
    public void testDrop_Constant() {
        setting.setPipelineFilterExpression("true");
        filter.open(setting);

        Assertions.assertTrue(filter.drop(new TestItem(1, 0, "")));
    }

    @Test
    public void testDrop_Expr() {
        setting.setPipelineFilterExpression("i > 0");
        filter.open(setting);

        Assertions.assertTrue(filter.drop(new TestItem(1, 0, "")));
        Assertions.assertFalse(filter.drop(new TestItem(-1, 0, "")));

    }

    @Getter
    private static class TestItem implements SpiderDataItem {
        private int i;
        private double d;
        private String s;

        public TestItem(int i, double d, String s) {
            this.i = i;
            this.d = d;
            this.s = s;
        }
    }
}
