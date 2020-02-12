package me.jspider.core.pipeline.exporter;

import me.jspider.base.bean.SpiderItem;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SequenceWriter;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import org.springframework.stereotype.Component;

import java.io.OutputStream;
import java.util.Objects;

@Component
public class CsvExporter extends BaseFileExporter {
    private SequenceWriter writer;

    @Override
    protected void process(SpiderItem item, OutputStream outputStream) throws Exception {
        if (Objects.isNull(writer)) {
            CsvMapper mapper = new CsvMapper();
            CsvSchema schema = mapper.schemaFor(item.getClass()).withHeader().withLineSeparator("\n");
            writer = mapper.writer(schema)
                    .without(JsonGenerator.Feature.AUTO_CLOSE_TARGET)
                    .writeValues(outputStream);
        }
        writer.write(item);
    }
}
