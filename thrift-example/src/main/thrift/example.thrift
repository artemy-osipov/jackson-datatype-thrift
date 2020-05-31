namespace java io.github.artemy.osipov.thrift.example

struct TestComplexStruct {
    1: string stringField
    2: bool boolField
    3: byte byteField
    4: i16 i16Field
    5: i32 i32Field
    6: i64 i64Field
    7: double doubleField
    8: TestEnum enumField
    9: binary binaryField
    10: TestSimpleStruct structField
    12: list<TestSimpleStruct> listStructField
    13: TestUnion unionField
}

struct TestSimpleStruct {
    1: bool f1
    2: string f2
}

enum TestEnum {
    ENUM_1
    ENUM_2
}

union TestUnion {
  1: TestEnum enumField
  2: TestSimpleStruct structField
}
