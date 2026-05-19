package io.github.artemy.osipov.thrift.jackson;

import tools.jackson.databind.module.SimpleModule;
import org.apache.thrift.TBase;
import org.apache.thrift.TUnion;

public class ThriftModule extends SimpleModule {

    public ThriftModule() {
        setDeserializers(new ThriftDeserializers());
        addSerializer(TBase.class, new TBaseSerializer());
        addSerializer(TUnion.class, new TUnionSerializer());
    }
}
