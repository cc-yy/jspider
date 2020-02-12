package me.jspider.core.pipeline.exporter;

import me.jspider.base.bean.SpiderDataItem;
import me.jspider.base.bean.SpiderItem;
import me.jspider.common.util.JsonUtils;
import me.jspider.base.bean.SpiderRuntimeException;
import me.jspider.core.pipeline.Pipeline;
import me.jspider.core.pipeline.PipelineChain;
import me.jspider.core.setting.Setting;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Objects;

/**
 * A {@link Pipeline} which exports {@link SpiderDataItem}s to files, one item per line.
 */
@Slf4j
public abstract class BaseFileExporter implements Pipeline {
    protected OutputStream outputStream;
    protected Charset outputCharset;

    @Override
    public void process(SpiderDataItem item, PipelineChain chain) {
        if (Objects.isNull(outputStream)) {
            return;
        }
        try {
            process(item, outputStream);
        } catch (Exception e) {
            log.error("Failed to export item: {}", JsonUtils.serialize(item), e);
        }

        chain.process(item);
    }

    @Override
    public void open(Setting setting) {
        String filename = setting.getPipelineExporterOutputFile();
        outputCharset = setting.getPipelineExporterOutputCharset();
        try {
            outputStream = new BufferedOutputStream(new FileOutputStream(filename));
        } catch (FileNotFoundException e) {
            log.error("Failed to create output file");
            throw new SpiderRuntimeException("unable to open output file");
        }
    }

    @Override
    public void close() {
        if (Objects.isNull(outputStream)) {
            return;
        }
        try {
            outputStream.flush();
        } catch (IOException ignored) { }
        try {
            outputStream.close();
        } catch (IOException ignored) { }
    }

    protected abstract void process(SpiderItem item, OutputStream outputStream) throws Exception;
}
