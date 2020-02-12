package me.jspider.core.parser.loader;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import me.jspider.base.bean.SpiderDataItem;
import me.jspider.base.bean.SpiderRuntimeException;
import me.jspider.core.parser.select.Selector;
import me.jspider.core.parser.select.Selectable;
import me.jspider.core.parser.select.Selection;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Objects;

@SuppressWarnings("unchecked")
public abstract class SpiderItemMapper<T extends SpiderDataItem> extends SpiderItemLoader {
    private final Class<T> clazz;
    private final Map<String, Selector> selectors;
    private final ObjectMapper mapper;

    protected SpiderItemMapper() {
        Type type = getClass().getGenericSuperclass();
        while (!(type instanceof ParameterizedType) || ((ParameterizedType) type).getRawType() != SpiderItemMapper.class) {
            if (type instanceof ParameterizedType) {
                type = ((Class<?>) ((ParameterizedType) type).getRawType()).getGenericSuperclass();
            } else {
                type = ((Class<?>) type).getGenericSuperclass();
            }
        }
        this.clazz = (Class<T>) ((ParameterizedType) type).getActualTypeArguments()[0];

        this.selectors = ImmutableMap.copyOf(extractSelectors());

        mapper = new ObjectMapper();
    }

    @Override
    protected Selector itemListSelector() {
        MapperSpec spec = clazz.getAnnotation(MapperSpec.class);
        if (Objects.isNull(spec)) {
            throw new SpiderRuntimeException("spec should be specified for " + clazz.getName());
        }
        Selector selector = getSelector(spec);
        if (Objects.isNull(selector)) {
            throw new SpiderRuntimeException("no selector found for spec: " + spec);
        }
        return selector;
    }

    @Override
    protected ItemMapper itemMapper() {
        return ((response, selectable) -> mapItem(selectable));
    }

    private Selector getSelector(MapperSpec spec) {
        if (StringUtils.isNotEmpty(spec.json())) {
            return new Selector.JsonSelector(spec.json());
        } else if (StringUtils.isNotEmpty(spec.xpath())) {
            return new Selector.XpathSelector(spec.xpath());
        } else if (StringUtils.isNotEmpty(spec.css())) {
            return new Selector.CssSelector(spec.css());
        } else {
            return null;
        }
    }

    private Map<String, Selector> extractSelectors() {
        Map<String, Selector> map = Maps.newHashMap();
        for (Field field : clazz.getDeclaredFields()) {
            MapperSpec spec = field.getAnnotation(MapperSpec.class);
            if (Objects.isNull(spec)) {
                continue;
            }
            Selector s = getSelector(spec);
            if (Objects.nonNull(s)) {
                map.put(field.getName(), s);
            }
        }
        return map;
    }

    private T mapItem(Selectable itemSelectable) {
        Map<String, Object> map = Maps.newHashMap();
        for (Map.Entry<String, Selector> entry : selectors.entrySet()) {
            Selection list = entry.getValue().select(itemSelectable);
            if (!list.isEmpty()) {
                map.put(entry.getKey(), list.text());
            }
        }
        try {
            return mapper.convertValue(map, clazz);
        } catch (Exception e) {
            return null;
        }
    }
}
