package io.github.artemy.osipov.thrift.jackson

import groovy.transform.CompileStatic
import io.github.artemy.osipov.thrift.example.*

@CompileStatic
class TestData {

    static TestComplexStruct thriftComplexStruct() {
        new TestComplexStruct().tap {
            stringField = 'some'
            boolField = true
            byteField = 1 as byte
            i16Field = 2 as short
            i32Field = 3
            i64Field = 4
            doubleField = 5.5d
            enumField = TestEnum.ENUM_2
            binaryField = [1, 2, 3] as byte[]
            structField = thriftSimpleStruct()
            listStructField = [thriftSimpleStruct()]
            unionField = new TestUnion().tap {
                enumField = TestEnum.ENUM_1
            }
        }
    }

    static TestSimpleStruct thriftSimpleStruct() {
        new TestSimpleStruct(true, 'f2')
    }

    static String jsonComplexStruct() {
        def thrift = thriftComplexStruct()
        """
          {
            "stringField": "$thrift.stringField",
            "boolField": $thrift.boolField,
            "byteField": $thrift.byteField,
            "i16Field": $thrift.i16Field,
            "i32Field": $thrift.i32Field,
            "i64Field": $thrift.i64Field,
            "doubleField": $thrift.doubleField,
            "enumField": "$thrift.enumField",
            "binaryField": "${thrift.binaryField.encodeBase64()}",
            "structField": ${jsonSimpleStruct()},
            "listStructField": [${jsonSimpleStruct()}],
            "unionField": { "enumField": "$thrift.unionField.enumField" }
          }
        """
    }

    static String jsonSimpleStruct() {
        def thrift = thriftSimpleStruct()
        """
          {
            "f1": $thrift.f1,
            "f2": "$thrift.f2"
          }
        """
    }
}
