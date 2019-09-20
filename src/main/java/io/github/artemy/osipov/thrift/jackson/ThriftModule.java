package io.github.artemy.osipov.thrift.jackson;

import com.fasterxml.jackson.databind.module.SimpleModule;
import org.apache.thrift.TBase;

public class ThriftModule extends SimpleModule {

    public ThriftModule() {
        addSerializer(TBase.class, new ThriftSerializer());
    }
}
