package io.github.artemy.osipov.thrift.jackson;

import com.fasterxml.jackson.databind.module.SimpleModule;
import org.apache.thrift.TBase;
import org.apache.thrift.TUnion;

public class ThriftModule extends SimpleModule {

    public ThriftModule() {
        addSerializer(TBase.class, new TBaseSerializer());
        addSerializer(TUnion.class, new TUnionSerializer());
    }
}
