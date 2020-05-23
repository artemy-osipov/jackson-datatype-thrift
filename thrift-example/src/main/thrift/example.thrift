namespace java io.github.artemy.osipov.thrift.example

struct SomeStruct {
    1: string stringField
    2: bool boolField
    3: i32 intField
    4: SomeEnum enumField
    5: SomeInnerStruct complexField
}

struct SomeInnerStruct {
    1: string f1
    2: string f2
}

enum SomeEnum {
    ENUM_1
    ENUM_2
}

union SomeUnion {
  1: SomeEnum enumField
  2: SomeInnerStruct structField
}
