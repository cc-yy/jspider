package me.jspider.core.base;

import me.jspider.core.setting.Setting;

/**
 * A component can be started and stopped.
 */
public interface StartStoppable {

    void start(Setting setting);

    void stop();
}
