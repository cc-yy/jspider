package me.jspider.base.bean;

import me.jspider.base.parser.SpiderParser;
import me.jspider.base.common.SpiderConstant;
import com.google.common.collect.Maps;
import lombok.Data;

import java.nio.charset.Charset;
import java.util.Map;

/**
 * An abstraction of request.
 */
@Data
public class SpiderRequest implements SpiderItem {
    private String url;
    private String method;
    private Map<String, String> headers;
    private Map<String, String> cookies;
    private byte[] body;
    private Charset charset;

    private SpiderParser parser;
    private CbArgs cbArgs;
    private int priority = 0;
    private boolean render;

    private int retry = 1;

    private SpiderRequest() {}

    public void setHeader(String name, String value) {
        this.headers.put(name, value);
    }

    public void setCookie(String name, String value) {
        this.cookies.put(name, value);
    }

    public void incrRetry() {
        this.retry++;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String url;
        private String method = "GET";
        private Map<String, String> headers = Maps.newHashMap();
        private Map<String, String> cookies = Maps.newHashMap();
        private byte[] body;
        private Charset charset = SpiderConstant.DEFAULT_CHARSET;
        private SpiderParser parser;
        private CbArgs cbArgs;
        private int priority = 0;
        private boolean render;
        private int retry = 1;

        private Builder() {
        }

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder method(String method) {
            this.method = method;
            return this;
        }

        public Builder headers(Map<String, String> headers) {
            this.headers = headers;
            return this;
        }

        public Builder cookies(Map<String, String> cookies) {
            this.cookies = cookies;
            return this;
        }

        public Builder body(byte[] body) {
            this.body = body;
            return this;
        }

        public Builder charset(Charset charset) {
            this.charset = charset;
            return this;
        }

        public Builder parser(SpiderParser parser) {
            this.parser = parser;
            return this;
        }

        public Builder cbArgs(CbArgs cbArgs) {
            this.cbArgs = cbArgs;
            return this;
        }

        public Builder priority(int priority) {
            this.priority = priority;
            return this;
        }

        public Builder render(boolean render) {
            this.render = render;
            return this;
        }

        public Builder retry(int retry) {
            this.retry = retry;
            return this;
        }

        public SpiderRequest build() {
            SpiderRequest spiderRequest = new SpiderRequest();
            spiderRequest.setUrl(url);
            spiderRequest.setMethod(method);
            spiderRequest.setHeaders(headers);
            spiderRequest.setCookies(cookies);
            spiderRequest.setBody(body);
            spiderRequest.setCharset(charset);
            spiderRequest.setParser(parser);
            spiderRequest.setCbArgs(cbArgs);
            spiderRequest.setPriority(priority);
            spiderRequest.setRender(render);
            spiderRequest.setRetry(retry);
            return spiderRequest;
        }
    }
}
