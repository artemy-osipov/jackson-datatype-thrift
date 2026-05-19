package io.github.artemy.osipov.thrift.jackson;

import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.deser.std.StdNodeBasedDeserializer;
import org.apache.thrift.TBase;
import org.apache.thrift.TFieldIdEnum;
import org.apache.thrift.meta_data.FieldMetaData;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;
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
    public T convert(JsonNode root, DeserializationContext ctxt) {
        try {
            T thrift = buildThrift();
            Map<String, ? extends TFieldIdEnum> thriftFields = extractThriftFields();

            for (Map.Entry<String, JsonNode> field : root.properties()) {
                if (thriftFields.containsKey(field.getKey())) {
                    Type thriftFieldType = resolveThriftFieldType(field.getKey());
                    Object value = resolveJsonNode(ctxt, field.getValue(), thriftFieldType);
                    setThriftField(thrift, thriftFields.get(field.getKey()), value);
                }
            }

            return thrift;
        } catch (Exception e) {
            throw new RuntimeException(e);
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

    private <P> P resolveJsonNode(DeserializationContext ctx, JsonNode value, Type type) {
        if (value.isNull()) {
            return null;
        }

        JavaType javaType = ctx.getTypeFactory().constructType(type);
        JsonParser parser = ctx.treeAsTokens(value);
        parser.nextToken();

        return ctx.readValue(parser, javaType);
    }
}
