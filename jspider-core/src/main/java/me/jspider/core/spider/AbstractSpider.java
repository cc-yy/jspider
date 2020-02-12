package me.jspider.core.spider;

import me.jspider.base.bean.SpiderRequest;
import me.jspider.core.setting.Setting;

/**
 * Base class for spiders.
 */
public abstract class AbstractSpider {
    /**
     * Init the spider.
     * Subclasses should never override this method, but those {@code protected} methods like {@link #updateSetting}.
     */
    public void init(Setting setting) {
        updateSetting(setting);
    }

    /**
     * The seed requests.
     */
    public abstract Iterable<SpiderRequest> seed();

    /**
     * Override this method to update spider settings
     */
    protected void updateSetting(Setting setting) {
    }
}
