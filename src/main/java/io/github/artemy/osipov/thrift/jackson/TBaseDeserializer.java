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
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TBaseDeserializer<T extends TBase<T, F>, F extends TFieldIdEnum> extends StdNodeBasedDeserializer<T> {

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
        String capitalizedField = Character.toUpperCase(field.charAt(0)) + field.substring(1);
        String getMethod = "get" + capitalizedField;
        String isMethod = "is" + capitalizedField;
        return Arrays.stream(thriftClass.getDeclaredMethods())
                .filter(method -> method.getName().equals(getMethod) || method.getName().equals(isMethod))
                .filter(method -> method.getParameterCount() == 0)
                .findFirst()
                .orElseThrow(() -> new NoSuchMethodException("getter method for field " + field + " has not found"))
                .getGenericReturnType();
    }

    private <P> P resolveJsonNode(DeserializationContext ctx, JsonNode value, Type type) throws IOException {
        if (value.isNull()) {
            return null;
        }

        JavaType javaType = ctx.getTypeFactory().constructType(type);
        JsonDeserializer<Object> deserializer = ctx.findRootValueDeserializer(javaType);

        JsonParser parser = value.traverse(ctx.getParser().getCodec());
        parser.nextToken();

        return (P) deserializer.deserialize(parser, ctx);
    }
}