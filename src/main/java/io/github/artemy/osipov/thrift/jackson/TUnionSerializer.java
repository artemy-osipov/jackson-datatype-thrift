package io.github.artemy.osipov.thrift.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.thrift.TUnion;

import java.io.IOException;

public class TUnionSerializer extends JsonSerializer<TUnion> {

    @Override
    public void serialize(TUnion value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        ObjectMapper mapper = (ObjectMapper) jgen.getCodec();
        ObjectNode thriftNode = mapper.createObjectNode();

        if (value.getSetField() != null) {
            thriftNode.set(
                    value.getSetField().getFieldName(),
                    mapper.valueToTree(value.getFieldValue())
            );
        }

        jgen.writeTree(thriftNode);
    }
}