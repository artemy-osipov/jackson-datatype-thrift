package io.github.artemy.osipov.thrift.jackson;

import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.node.ObjectNode;
import org.apache.thrift.TBase;
import org.apache.thrift.TFieldIdEnum;
import org.apache.thrift.meta_data.FieldMetaData;


public class TBaseSerializer<T extends TBase<T, F>, F extends TFieldIdEnum> extends ValueSerializer<T> {

    @Override
    public void serialize(T value, JsonGenerator jgen, SerializationContext provider) {
        ObjectNode thriftNode = (ObjectNode) provider.createObjectNode();

        Class<T> thriftClass = (Class<T>) value.getClass();
        FieldMetaData.getStructMetaDataMap(thriftClass)
                .keySet()
                .forEach(meta -> thriftNode.set(
                        meta.getFieldName(),
                        provider.valueToTree(value.getFieldValue(meta))
                ));

        jgen.writeTree(thriftNode);
    }
}
