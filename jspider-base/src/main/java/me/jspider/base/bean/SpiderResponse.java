package me.jspider.base.bean;

import me.jspider.base.common.SpiderConstant;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.ArrayUtils;

import java.nio.charset.Charset;
import java.util.Map;
import java.util.Objects;

/**
 * An abstraction of request.
 */
@Data
public class SpiderResponse implements SpiderItem {
    private String url;
    private int status;
    private Map<String, String> headers;
    private Map<String, String> cookies;
    @Setter(AccessLevel.NONE)
    private byte[] body;
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private String text;
    private Charset charset;

    private SpiderRequest request;

    public void setBody(byte[] body) {
        this.body = body;
        this.text = null;
    }

    public String getText() {
        if (Objects.isNull(text) && ArrayUtils.isNotEmpty(body)) {
            text = new String(body, charset);
        }
        return text;
    }

    public CbArgs getCbArgs() {
        return request.getCbArgs();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static SpiderResponse fail(String url) {
        return SpiderResponse.builder()
                .url(url)
                .status(-1)
                .build();
    }

    public static SpiderResponse fail(SpiderRequest request) {
        return fail(request, -1);
    }

    public static SpiderResponse fail(SpiderRequest request, int status) {
        return SpiderResponse.builder()
                .url(request.getUrl())
                .request(request)
                .status(status)
                .build();
    }

    public static final class Builder {
        private String url;
        private int status;
        private Map<String, String> headers;
        private Map<String, String> cookies;
        private byte[] body;
        private Charset charset = SpiderConstant.DEFAULT_CHARSET;
        private SpiderRequest request;

        private Builder() {
        }


        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder status(int status) {
            this.status = status;
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

        public Builder request(SpiderRequest request) {
            this.request = request;
            return this;
        }

        public SpiderResponse build() {
            SpiderResponse spiderResponse = new SpiderResponse();
            spiderResponse.headers = this.headers;
            spiderResponse.cookies = this.cookies;
            spiderResponse.request = this.request;
            spiderResponse.charset = this.charset;
            spiderResponse.body = this.body;
            spiderResponse.url = this.url;
            spiderResponse.status = this.status;
            return spiderResponse;
        }
    }
}
