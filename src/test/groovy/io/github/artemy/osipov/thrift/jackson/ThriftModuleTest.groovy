package io.github.artemy.osipov.thrift.jackson

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.artemy.osipov.thrift.example.SomeUnion
import io.github.artemy.osipov.thrift.example.SomeEnum
import io.github.artemy.osipov.thrift.example.SomeInnerStruct
import io.github.artemy.osipov.thrift.example.SomeStruct
import org.junit.jupiter.api.Test

class ThriftModuleTest {

    def mapper = new ObjectMapper().tap {
        registerModule(new ThriftModule())
    }

    @Test
    void "should serialize thrift struct to json"() {
        def thrift = [thriftStruct(), thriftStruct()]

        def res = mapper.valueToTree(thrift)

        assert res == mapper.readTree("""
            [
              ${thriftStructJson()},         
              ${thriftStructJson()}         
            ]
        """)
    }

    @Test
    void "should serialize thrift union to json"() {
        def thrift = new SomeUnion().tap {
            enumField = SomeEnum.ENUM_2
        }

        def res = mapper.valueToTree(thrift)

        assert res == mapper.readTree("""
            {
              "enumField": "${SomeEnum.ENUM_2}"
            }
        """)
    }

    @Test
    void "should serialize thrift union with struct to json"() {
        def thrift = new SomeUnion().tap {
            structField = thriftInnerStruct()
        }

        def res = mapper.valueToTree(thrift)

        assert res == mapper.readTree("""
            {
              "structField": ${thriftInnerStructJson()}
            }
        """)
    }

    @Test
    void "should serialize thrift empty union to json"() {
        def thrift = new SomeUnion()

        def res = mapper.valueToTree(thrift)

        assert res == mapper.readTree('{}')
    }

    def thriftStruct() {
        new SomeStruct().tap {
            stringField = 'some'
            boolField = true
            intField = 42
            enumField = SomeEnum.ENUM_2
            complexField = thriftInnerStruct()
        }
    }

    def thriftInnerStruct() {
        new SomeInnerStruct('f1', 'f2')
    }

    String thriftStructJson() {
        """
        {
          "stringField": "some",
          "boolField": true,
          "intField": 42,
          "enumField": "$SomeEnum.ENUM_2",
          "complexField": ${thriftInnerStructJson()}          
        }
        """
    }

    String thriftInnerStructJson() {
        """
        {
          "f1": "f1",
          "f2": "f2"
        } 
        """
    }
}