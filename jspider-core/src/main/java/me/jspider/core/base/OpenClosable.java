package me.jspider.core.base;

import me.jspider.core.setting.Setting;

/**
 * A component can be opened and closed.
 */
public interface OpenClosable {
    void open(Setting setting);

    void close();
}
