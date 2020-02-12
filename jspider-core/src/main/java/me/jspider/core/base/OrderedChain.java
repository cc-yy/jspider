package me.jspider.core.base;

import com.google.common.collect.Lists;
import lombok.Getter;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * A chain holding items with order.
 * Item with larger order is closer to the head of the chain.
 *
 * @param <T>
 */
public class OrderedChain<T> implements Iterable<T> {
    private static final int DEFAULT_PRIORITY = Ordered.LOWEST_PRECEDENCE;

    private final List<Holder<T>> chain = Lists.newArrayList();

    public void add(T item) {
        Order o = item.getClass().getAnnotation(Order.class);
        if (Objects.isNull(o)) {
            this.add(new Holder<>(item, DEFAULT_PRIORITY));
        } else {
            this.add(new Holder<>(item, o.value()));
        }
    }

    public void add(T item, int order) {
        this.add(new Holder<>(item, order));
    }

    private void add(Holder<T> holder) {
        synchronized (this.chain) {
            int i = 0;
            for (Holder h : this.chain) {
                if (holder.getOrder() < h.getOrder()) {
                    break;
                }
                ++i;
            }
            this.chain.add(i, holder);
        }
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            Iterator<Holder<T>> iter =  chain.iterator();
            @Override
            public boolean hasNext() {
                return iter.hasNext();
            }

            @Override
            public T next() {
                return iter.next().getItem();
            }
        };
    }

    @Getter
    private static final class Holder<T> {
        private T item;
        private int order;

        Holder(T item, int order) {
            this.item = item;
            this.order = order;
        }
    }
}
