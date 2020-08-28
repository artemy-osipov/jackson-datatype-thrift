package io.github.artemy.osipov.thrift.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.SneakyThrows;
import org.apache.thrift.TBase;
import org.apache.thrift.TFieldIdEnum;
import org.apache.thrift.meta_data.FieldMetaData;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TBaseSerializer extends JsonSerializer<TBase> {

    private static final Pattern GETTER_PATTERN = Pattern.compile("(get|is)([A-Z].*)");

    @Override
    public void serialize(TBase value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        ObjectMapper mapper = (ObjectMapper) jgen.getCodec();
        ObjectNode thriftNode = mapper.createObjectNode();

        extractFields(value, mapper)
                .forEach(thriftNode::set);

        jgen.writeTree(thriftNode);
    }

    private Map<String, JsonNode> extractFields(TBase value, ObjectMapper mapper) {
        Map<String, JsonNode> accum = new LinkedHashMap<>();
        fillFields(accum, value.getClass(), value, mapper);
        return accum;
    }

    private <C extends TBase> void fillFields(Map<String, JsonNode> accum, Class<C> currentClass, TBase value, ObjectMapper mapper) {
        Map<? extends TFieldIdEnum, FieldMetaData> structMetaDataMap = FieldMetaData.getStructMetaDataMap(currentClass);

        if (structMetaDataMap == null) {
            accum.putAll(extractDeclaredFields(currentClass, value, mapper));
            fillFields(accum, (Class<? extends TBase>) currentClass.getSuperclass(), value, mapper);
        } else {
            structMetaDataMap.keySet()
                    .forEach(meta -> accum.put(
                            meta.getFieldName(),
                            mapper.valueToTree(value.getFieldValue(meta))
                    ));
        }
    }

    private Map<String, JsonNode> extractDeclaredFields(Class<?> currentClass, TBase value, ObjectMapper mapper) {
        return Stream.of(currentClass.getDeclaredMethods())
                .filter(method -> Modifier.isPublic(method.getModifiers()))
                .filter(method -> method.getParameters().length == 0)
                .filter(method -> !method.getReturnType().equals(Void.TYPE))
                .filter(method -> GETTER_PATTERN.matcher(method.getName()).find())
                .collect(Collectors.toMap(
                        this::extractFieldName,
                        method -> extractFieldValue(method, value, mapper)
                ));
    }

    private String extractFieldName(Method method) {
        Matcher matcher = GETTER_PATTERN.matcher(method.getName());
        matcher.find();
        String fieldNameSuffix = matcher.group(2);
        return fieldNameSuffix.substring(0, 1).toLowerCase() + fieldNameSuffix.substring(1);
    }

    @SneakyThrows
    private JsonNode extractFieldValue(Method method, TBase value, ObjectMapper mapper) {
        return mapper.valueToTree(
                method.invoke(value)
        );
    }
}