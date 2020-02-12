package me.jspider.core.parser.select;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Implementations:
 *  <li>json path: <a href="https://github.com/json-path/JsonPath">github/JsonPath</a></li>
 */
@EqualsAndHashCode
public class JsonDocument implements Selectable {
    private final DocumentContext context;

    public static JsonDocument build(Object object) {
        if (object instanceof String) {
            return new JsonDocument(JsonPath.parse((String) object));
        }
        return new JsonDocument(JsonPath.parse(object));
    }


    private JsonDocument(DocumentContext context) {
        this.context = context;
    }

    @Override
    public Selection css(String selector) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Selection xpath(String xpath) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Selection json(String json) {
        Selection selection = new Selection();

        if (StringUtils.isEmpty(json)) {
            selection.add(this);
            return selection;
        }

        Object obj;
        try {
            obj = context.read(json);
        } catch (PathNotFoundException e) {
            obj = Collections.emptyList();
        }

        if (Objects.isNull(obj)) {
            selection.add(Null.build());
        } else if (obj instanceof List) {
            return ((List<?>) obj).stream().map(JsonDocument::build).collect(Collectors.toCollection(Selection::new));
        } else {
            selection.add(JsonDocument.build(obj));
        }
        return selection;
    }

    @Override
    public Object repr() {
        return context.json();
    }

    @Override
    public String text() {
        return context.json().toString();
    }
}
