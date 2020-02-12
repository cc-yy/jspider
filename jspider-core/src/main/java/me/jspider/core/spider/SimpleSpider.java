package me.jspider.core.spider;

import me.jspider.base.bean.SpiderDataItem;
import me.jspider.base.bean.SpiderItem;
import me.jspider.base.parser.SpiderParser;
import me.jspider.base.bean.SpiderRequest;
import me.jspider.base.bean.SpiderResponse;
import me.jspider.core.base.Spider;
import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.List;
import java.util.stream.Collectors;

@Spider(value = "simple")
public class SimpleSpider extends AbstractSpider {
    @Override
    public Iterable<SpiderRequest> seed() {
        return Lists.newArrayList(SpiderRequest.builder().url("https://www.baidu.com").parser(new TitleParser()).build());
    }

    private static class TitleParser implements SpiderParser {

        @Override
        public List<SpiderItem> parse(SpiderResponse response) {
            Document doc = Jsoup.parse(response.getText());
            Elements elements = doc.select("title");
            return elements.stream().map(e -> {
                TitleItem it = new TitleItem();
                it.setTitle(e.text());
                return it;
            }).collect(Collectors.toList());
        }
    }

    @Getter
    @Setter
    private static class TitleItem implements SpiderDataItem {
        private String title;
    }
}
