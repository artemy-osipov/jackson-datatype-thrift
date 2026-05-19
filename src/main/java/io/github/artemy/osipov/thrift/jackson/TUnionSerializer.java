package io.github.artemy.osipov.thrift.jackson;

import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.node.ObjectNode;
import org.apache.thrift.TUnion;

public class TUnionSerializer extends ValueSerializer<TUnion> {

    @Override
    public void serialize(TUnion value, JsonGenerator jgen, SerializationContext provider) {
        ObjectNode thriftNode = (ObjectNode) provider.createObjectNode();

        if (value.getSetField() != null) {
            thriftNode.set(
                    value.getSetField().getFieldName(),
                    provider.valueToTree(value.getFieldValue())
            );
        }

        jgen.writeTree(thriftNode);
    }
}
