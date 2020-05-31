package io.github.artemy.osipov.thrift.jackson

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.artemy.osipov.thrift.example.TestUnion
import io.github.artemy.osipov.thrift.example.TestEnum
import org.junit.jupiter.api.Test

import static io.github.artemy.osipov.thrift.jackson.TestData.*

class ThriftSerializeTest {

    def mapper = new ObjectMapper().tap {
        registerModule(new ThriftModule())
    }

    @Test
    void "should serialize complex thrift struct to json"() {
        def res = mapper.valueToTree(thriftComplexStruct())

        assert res.toString() == mapper.readTree(jsonComplexStruct()).toString()
    }

    @Test
    void "should serialize list thrift struct to json"() {
        def thrift = [thriftSimpleStruct(), thriftSimpleStruct()]

        def res = mapper.valueToTree(thrift)

        assert res == mapper.readTree("""
            [
              ${jsonSimpleStruct()},         
              ${jsonSimpleStruct()}         
            ]
        """)
    }

    @Test
    void "should serialize thrift union to json"() {
        def thrift = new TestUnion().tap {
            enumField = TestEnum.ENUM_2
        }

        def res = mapper.valueToTree(thrift)

        assert res == mapper.readTree("""
            {
              "enumField": "${TestEnum.ENUM_2}"
            }
        """)
    }

    @Test
    void "should serialize thrift union with struct to json"() {
        def thrift = new TestUnion().tap {
            structField = thriftSimpleStruct()
        }

        def res = mapper.valueToTree(thrift)

        assert res == mapper.readTree("""
            {
              "structField": ${jsonSimpleStruct()}
            }
        """)
    }

    @Test
    void "should serialize thrift empty union to json"() {
        def thrift = new TestUnion()

        def res = mapper.valueToTree(thrift)

        assert res == mapper.readTree('{}')
    }
}