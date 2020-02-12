package me.jspider.core.base;

import com.google.common.collect.Lists;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.core.annotation.Order;

import java.util.List;

public class OrderedChainTest {

    @Test
    public void testAdd() {
        OrderedChain<Integer> chain = new OrderedChain<>();
        chain.add(1);
        chain.add(2);
        chain.add(10, 10);

        List<Integer> list = Lists.newArrayList(chain.iterator());
        Assertions.assertEquals(3, list.size());
        Assertions.assertEquals(1, list.get(1).intValue());
        Assertions.assertEquals(10, list.get(0).intValue());
    }

    @Test
    public void testAdd_WithAnnotation() {
        OrderedChain<Object> chain = new OrderedChain<>();
        chain.add(1);
        chain.add(new OrderedClass(2));

        List<Object> list = Lists.newArrayList(chain.iterator());
        Assertions.assertEquals(2, list.size());
        Assertions.assertEquals(1, list.get(1));
        Assertions.assertTrue(list.get(0) instanceof OrderedClass);
    }

    @Order(1)
    private static class OrderedClass {
        int i;

        OrderedClass(int i) {
            this.i = i;
        }
    }
}
