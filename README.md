Jackson datatype module to support JSON serialization/deserialization of Thrift objects

![Maven Central](https://img.shields.io/maven-central/v/io.github.artemy-osipov.thrift/jackson-datatype-thrift)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/b6fc3f219d9e41cc8efd4daa7ba329ee)](https://www.codacy.com/manual/osipov.artemy/jackson-datatype-thrift?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=artemy-osipov/jackson-datatype-thrift&amp;utm_campaign=Badge_Grade)

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
SomeStruct thrift = new SomeStruct()
                .setStringField("s1")
                .setBoolField(true)
                .setIntField(1)
                .setEnumField(SomeEnum.ENUM_1)
                .setComplexField(new SomeInnerStruct()
                        .setF1("f1")
                        .setF2("f2")
                );

String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(thrift);
assert json.equals("""
                {
                  "stringField": "s1",
                  "boolField": true,
                  "intField": 1,
                  "enumField": "ENUM_1",
                  "complexField": {
                    "f1": "f1",
                    "f2": "f2"
                  }
                }""");
```

deserialization will be:

```java
String json = """
              {
                "stringField": "s1",
                "boolField": true,
                "intField": 1,
                "enumField": "ENUM_1",
                "complexField": {
                  "f1": "f1",
                  "f2": "f2"
                }
               }""";

String thrift = mapper.readValue(json, SomeStruct.class);
assert thrift.equals(new SomeStruct()
                     .setStringField("s1")
                     .setBoolField(true)
                     .setIntField(1)
                     .setEnumField(SomeEnum.ENUM_1)
                     .setComplexField(
                       new SomeInnerStruct()
                         .setF1("f1")
                         .setF2("f2")
                     ));
```