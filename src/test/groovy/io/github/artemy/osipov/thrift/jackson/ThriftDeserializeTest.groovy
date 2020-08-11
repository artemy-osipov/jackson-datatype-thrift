package io.github.artemy.osipov.thrift.jackson

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import io.github.artemy.osipov.thrift.example.TestComplexStruct
import io.github.artemy.osipov.thrift.example.TestEnum
import io.github.artemy.osipov.thrift.example.TestSimpleStruct
import io.github.artemy.osipov.thrift.example.TestUnion
import org.junit.jupiter.api.Test

import static io.github.artemy.osipov.thrift.jackson.TestData.*

class ThriftDeserializeTest {

    def mapper = new ObjectMapper().tap {
        registerModule(new ThriftModule())
    }

    @Test
    void "should deserialize complex thrift struct from json"() {
        def res = mapper.readValue(jsonComplexStruct(), TestComplexStruct)

        assert res == thriftComplexStruct()
    }

    @Test
    void "should deserialize null values as none"() {
        def res = mapper.readValue('{ "boolField": null, "enumField": null }', TestComplexStruct)

        assert res.enumField == null
        assert !res.setEnumField
        assert !res.boolField
        assert !res.setBoolField
    }

    @Test
    void "should deserialize list thrift struct from json"() {
        def res = mapper.readValue(
                "[${jsonSimpleStruct()}, ${jsonSimpleStruct()}]",
                new TypeReference<List<TestSimpleStruct>>() {
                }
        )

        assert res == [thriftSimpleStruct(), thriftSimpleStruct()]
    }

    @Test
    void "should deserialize thrift union from json"() {
        def res = mapper.readValue("""
            {
              "enumField": "${TestEnum.ENUM_2}"
            }
        """, TestUnion)

        assert res == new TestUnion().tap {
            enumField = TestEnum.ENUM_2
        }
    }

    @Test
    void "should deserialize thrift union with struct from json"() {
        def res = mapper.readValue("""
            {
              "structField": ${jsonSimpleStruct()}
            }
        """, TestUnion)

        assert res == new TestUnion().tap {
            structField = thriftSimpleStruct()
        }
    }

    @Test
    void "should deserialize thrift empty union from json"() {
        def res = mapper.readValue('{}', TestUnion)

        assert res.setField == null
        assert res.fieldValue == null
    }
}