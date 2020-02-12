package me.jspider.core.pipeline.filter;

import me.jspider.base.bean.SpiderDataItem;
import me.jspider.base.bean.SpiderRuntimeException;
import me.jspider.core.setting.Setting;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.StringWriter;
import java.util.Locale;

/**
 * An implementation of {@link BaseItemFilter} based on SpEL.
 * Item against which the expression evaluates as an empty string is dropped.
 * Reference: <a href=https://freemarker.apache.org/docs/dgui_quickstart.html>Freemarker documentation</a>
 */
@Slf4j
public class FreemarkerItemFilter extends BaseItemFilter {
    private Template template;

    @Override
    public void open(Setting setting) {
        super.open(setting);
        if (!active) {
            return;
        }
        try {
            Configuration cfg = new Configuration(Configuration.VERSION_2_3_29);
            cfg.setWrapUncheckedExceptions(true);
            cfg.setLocale(Locale.CHINA);
            cfg.setObjectWrapper(new DefaultObjectWrapper(Configuration.VERSION_2_3_29));
            StringTemplateLoader loader = new StringTemplateLoader();
            loader.putTemplate(FreemarkerItemFilter.class.getSimpleName(), expr);
            cfg.setTemplateLoader(loader);
            template = cfg.getTemplate(FreemarkerItemFilter.class.getSimpleName());
        } catch (Exception e) {
            log.error("Error occurred when parsing freemarker: {}", expr, e);
            throw new SpiderRuntimeException("illegal freemarker" + expr, e);
        }

    }

    @Override
    protected boolean drop(SpiderDataItem item) {
        try {
            StringWriter writer = new StringWriter();
            template.process(item, writer);
            return StringUtils.isEmpty(writer.toString());
        } catch (Exception e) {
            log.error("Error occurred when evaluating freemarker: {}, item: {}", expr, item, e);
            return true;
        }
    }
}
