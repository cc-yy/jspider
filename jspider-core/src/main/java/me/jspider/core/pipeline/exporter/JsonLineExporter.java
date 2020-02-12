package me.jspider.core.pipeline.exporter;

import me.jspider.base.bean.SpiderItem;
import me.jspider.core.setting.Setting;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.OutputStream;

/**
 * Exports items in json.
 */
@Component
@Slf4j
public class JsonLineExporter extends BaseFileExporter {
    private ObjectWriter writer;

    @Override
    public void open(Setting setting) {
        super.open(setting);

        ObjectMapper mapper = new ObjectMapper();
        writer = mapper.writer().without(JsonGenerator.Feature.AUTO_CLOSE_TARGET);
    }

    @Override
    protected void process(SpiderItem item, OutputStream outputStream) throws Exception {
        writer.writeValue(outputStream, item);
        outputStream.write('\n');
    }
}
