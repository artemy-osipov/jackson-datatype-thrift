package io.github.artemy.osipov.thrift.jackson;

import io.github.artemy.osipov.thrift.example.TestSimpleStruct;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class TestExtendedStruct extends TestSimpleStruct {

    private String extendedString;

    private boolean extendedBool;
    private ExtendedDTO extendedDTO;

    @Data
    public static class ExtendedDTO {
        private int dtoInt;
    }
}
