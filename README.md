Jackson datatype module to support JSON serialization of Thrift objects

### Maven dependency

To use module on Maven-based projects, use following dependency:

```xml
<dependency>
  <groupId>io.github.artemy.osipov.thrift</groupId>
  <artifactId>jackson-datatype-thrift</artifactId>
  <version>0.1.0</version> // or any newer version
</dependency>   
```

### Usage

To use thrift datatype you will need to register the module:

```java
ObjectMapper mapper = new ObjectMapper();
mapper.registerModule(new ThriftModule());
```

For thrift with scheme:

```thrift
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
```

serialization will be:

```java
SomeStruct some = new SomeStruct()
                .setStringField("s1")
                .setBoolField(true)
                .setIntField(1)
                .setEnumField(SomeEnum.ENUM_1)
                .setComplexField(new SomeInnerStruct()
                        .setF1("f1")
                        .setF2("f2")
                );

String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(some);
assert json.equals("{\n" +
                "  \"stringField\" : \"s1\",\n" +
                "  \"boolField\" : true,\n" +
                "  \"intField\" : 1,\n" +
                "  \"enumField\" : \"ENUM_1\",\n" +
                "  \"complexField\" : {\n" +
                "    \"f1\" : \"f1\",\n" +
                "    \"f2\" : \"f2\"\n" +
                "  }\n" +
                "}");
```

