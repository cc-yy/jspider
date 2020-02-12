package me.jspider.core.parser.loader;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import me.jspider.base.bean.SpiderDataItem;
import me.jspider.base.bean.SpiderItem;
import me.jspider.base.bean.SpiderResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

public class SpiderItemMapperTest {
    @Test
    public void testParse_ByJson() {
        SpiderResponse response = SpiderResponse.builder()
                .body("{\"id\":1,\"result\":[{\"name\":\"a\",\"data\":[1,2,3]},{\"name\":\"b\",\"data\":[2]},{\"name\":\"c\",\"data\":[]}]}".getBytes())
                .build();

        List<SpiderItem> items = Lists.newArrayList(new SpiderItemMapper<ItemByJson>(){}.parse(response));
        ItemByJson item1 = new ItemByJson("a", 1);
        ItemByJson item2 = new ItemByJson("b", 2);
        ItemByJson item3 = new ItemByJson("c", 0);
        Assertions.assertEquals(Sets.newHashSet(item1, item2, item3),
                items.stream().filter(i -> i instanceof ItemByJson).collect(Collectors.toSet()));
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    @MapperSpec(json = "$.result[*]")
    @ToString
    private static final class ItemByJson implements SpiderDataItem {
        @MapperSpec(json = "$.name")
        public String name;
        @MapperSpec(json = "$.data[0]")
        public int data;
    }
}
