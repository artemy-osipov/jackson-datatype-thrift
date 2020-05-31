package io.github.artemy.osipov.thrift.jackson;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.module.SimpleDeserializers;
import org.apache.thrift.TBase;

public class ThriftDeserializers extends SimpleDeserializers {

    @Override
    public JsonDeserializer<?> findBeanDeserializer(
            JavaType type,
            DeserializationConfig config,
            BeanDescription beanDesc) throws JsonMappingException {
        JsonDeserializer<?> customDeserializer = super.findBeanDeserializer(type, config, beanDesc);

        if (customDeserializer != null) {
            return customDeserializer;
        }

        if (TBase.class.isAssignableFrom(type.getRawClass())) {
            return new TBaseDeserializer<>(type);
        }

        return null;
    }
}
