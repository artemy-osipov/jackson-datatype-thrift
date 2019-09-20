package io.github.artemy.osipov.thrift.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.thrift.TBase;
import org.apache.thrift.meta_data.FieldMetaData;

import java.io.IOException;

public class ThriftSerializer extends JsonSerializer<TBase> {

    @Override
    public void serialize(TBase value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        ObjectMapper mapper = (ObjectMapper) jgen.getCodec();
        ObjectNode thriftNode = mapper.createObjectNode();

        FieldMetaData.getStructMetaDataMap(value.getClass())
                .keySet()
                .forEach(meta ->
                        thriftNode.set(
                                meta.getFieldName(),
                                mapper.valueToTree(value.getFieldValue(meta))));

        jgen.writeTree(thriftNode);
    }
}