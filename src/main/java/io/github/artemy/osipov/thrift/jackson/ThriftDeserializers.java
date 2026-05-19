package io.github.artemy.osipov.thrift.jackson;

import tools.jackson.databind.BeanDescription;
import tools.jackson.databind.DatabindException;
import tools.jackson.databind.DeserializationConfig;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.module.SimpleDeserializers;
import org.apache.thrift.TBase;

public class ThriftDeserializers extends SimpleDeserializers {

    @Override
    public ValueDeserializer<?> findBeanDeserializer(
            JavaType type,
            DeserializationConfig config,
            BeanDescription.Supplier beanDesc) throws DatabindException {
        ValueDeserializer<?> customDeserializer = super.findBeanDeserializer(type, config, beanDesc);

        if (customDeserializer != null) {
            return customDeserializer;
        }

        if (TBase.class.isAssignableFrom(type.getRawClass())) {
            return new TBaseDeserializer<>(type);
        }

        return null;
    }
}
