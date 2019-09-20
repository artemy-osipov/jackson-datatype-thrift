package io.github.artemy.osipov.thrift.jackson

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.Test
import io.github.artemy.osipov.thrift.example.SomeEnum
import io.github.artemy.osipov.thrift.example.SomeInnerStruct
import io.github.artemy.osipov.thrift.example.SomeStruct

class ThriftModuleTest {

    def mapper = new ObjectMapper().tap {
        registerModule(new ThriftModule())
    }

    @Test
    void "thrift module should trim thrift specific fields"() {
        def some = mapper.writeValueAsString(thriftStruct())
        def res = mapper.valueToTree([thriftStruct(), thriftStruct()])

        assert res == mapper.createArrayNode()
                .add(restStruct())
                .add(restStruct())
    }

    def thriftStruct() {
        new SomeStruct().tap {
            stringField = 'some'
            boolField = true
            intField = 42
            enumField = SomeEnum.ENUM_2
            complexField = new SomeInnerStruct('f1', 'f2')
        }
    }

    def restStruct() {
        def thrift = thriftStruct()
        mapper.createObjectNode()
                .put('stringField', thrift.stringField)
                .put('boolField', thrift.boolField)
                .put('intField', thrift.intField)
                .put('enumField', thrift.enumField.name())
                .set('complexField',
                        mapper.createObjectNode()
                                .put('f1', thrift.complexField.f1)
                                .put('f2', thrift.complexField.f2)
                )
    }
}