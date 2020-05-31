package io.github.artemy.osipov.thrift.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdNodeBasedDeserializer;
import org.apache.thrift.TBase;
import org.apache.thrift.TFieldIdEnum;
import org.apache.thrift.meta_data.FieldMetaData;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TBaseDeserializer<T extends TBase<?, ?>> extends StdNodeBasedDeserializer<T> {

    private final Class<T> thriftClass;

    protected TBaseDeserializer(JavaType targetType) {
        super(targetType);
        thriftClass = (Class<T>) _valueClass;
    }

    @Override
    public T convert(JsonNode root, DeserializationContext ctxt) throws IOException {
        try {
            T thrift = buildThrift();
            Map<String, ? extends TFieldIdEnum> thriftFields = extractThriftFields();

            Iterator<Map.Entry<String, JsonNode>> fields = root.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> field = fields.next();

                if (thriftFields.containsKey(field.getKey())) {
                    Type thriftFieldType = resolveThriftFieldType(field.getKey());
                    Object value = resolveJsonNode(ctxt, field.getValue(), thriftFieldType);
                    setThriftField(thrift, thriftFields.get(field.getKey()), value);
                }
            }

            return thrift;
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    private T buildThrift() throws ReflectiveOperationException {
        return thriftClass.getDeclaredConstructor().newInstance();
    }

    private Map<String, ? extends TFieldIdEnum> extractThriftFields() {
        return FieldMetaData.getStructMetaDataMap(thriftClass)
                .keySet()
                .stream()
                .collect(Collectors.toMap(TFieldIdEnum::getFieldName, Function.identity()));
    }

    private void setThriftField(T thrift, TFieldIdEnum field, Object value) throws ReflectiveOperationException {
        Method setter = thriftClass.getMethod("setFieldValue", TFieldIdEnum.class, Object.class);
        setter.invoke(thrift, field, value);
    }

    private Type resolveThriftFieldType(String field) throws NoSuchMethodException {
        Method getter;
        try {
            String getterName = "get" + field.substring(0, 1).toUpperCase() + field.substring(1);
            getter = thriftClass.getDeclaredMethod(getterName);
        } catch (NoSuchMethodException e) {
            String getterName = "is" + field.substring(0, 1).toUpperCase() + field.substring(1);
            getter = thriftClass.getDeclaredMethod(getterName);
        }

        return getter.getGenericReturnType();
    }

    private <P> P resolveJsonNode(DeserializationContext ctxt, JsonNode value, Type type) throws IOException {
        JavaType javaType = ctxt.getTypeFactory().constructType(type);
        JsonDeserializer<Object> deserializer = ctxt.findRootValueDeserializer(javaType);

        JsonParser parser = value.traverse(ctxt.getParser().getCodec());
        parser.nextToken();

        return (P) deserializer.deserialize(parser, ctxt);
    }
}