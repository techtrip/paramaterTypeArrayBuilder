package com.techtrip.examples.techtrip.dbparams.model;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

import java.sql.Types;
import java.time.LocalDate;
import java.util.function.Function;
import java.util.function.Predicate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


class ParameterTypeArrayBuilderTest {

    @Test
    void parameterTypeArrayBuilderMaintainsOrderAndTypeMappings() {

        LocalDate now = LocalDate.now();

        ParameterTypeArrayBuilder pMapper = ParameterTypeArrayBuilder.newBuilder()
                .parameterWihType("SOME String", Types.VARCHAR)
                .parameterWihType(36, Types.INTEGER)
                .parameterWihType(now, Types.TIMESTAMP)
                .build();

        Object[] parameters = pMapper.getParameter();
        int[] parameterTypes = pMapper.getParameterTypes();

        assertThat(parameters[0]).isEqualTo("SOME String");
        assertThat(parameterTypes[0]).isEqualTo(Types.VARCHAR);

        assertThat(parameters[1]).isEqualTo(36);
        assertThat(parameterTypes[1]).isEqualTo(Types.INTEGER);

        assertThat(parameters[2]).isEqualTo(now);
        assertThat(parameterTypes[2]).isEqualTo(Types.TIMESTAMP);
    }

    @Test
    void parameterTypeMapperFunctionsCalculatesAResultForInsert() {

        Predicate<String> stringStartWithT = s -> s.startsWith("S");

        ParameterTypeArrayBuilder pMapper = ParameterTypeArrayBuilder.newBuilder()
                .parameterWihType(stringStartWithT.test("SOME String"), Types.BOOLEAN)
                .parameterWihType(stringStartWithT.test("Not SOME String"), Types.BOOLEAN)
                .build();

        Object[] parameters = pMapper.getParameter();
        int[] parameterTypes = pMapper.getParameterTypes();

        assertTrue((boolean)parameters[0]);
        assertThat(parameterTypes[0]).isEqualTo(Types.BOOLEAN);

        assertFalse((boolean)parameters[1]);
        assertThat(parameterTypes[1]).isEqualTo(Types.BOOLEAN);
    }

    @Test
    void parameterTypeMapperFunctionsMutateValuesForFunctionalInterface() {
        LocalDate now = LocalDate.now();

        /**
         * Alternately we could inline the following but I am doing this here for
         * Demonstration purposes
         *
         * The function here guards against nulls and replaces them with a value
         */
        Function<String,String> contrivedNullToEmptyStringFunction = i -> { if (StringUtils.isEmpty(i)) return ""; return i;};

        ParameterTypeArrayBuilder pMapper = ParameterTypeArrayBuilder.newBuilder()
                .parameterWihType(null, Types.VARCHAR, contrivedNullToEmptyStringFunction) // MUTATION!! Null to ""
                .parameterWihType("Test", Types.VARCHAR, contrivedNullToEmptyStringFunction) // Do Nothing
                .parameterWihType(1000, Types.VARCHAR, Object::toString) // Int in String Out
                .parameterWihType("NormalNoMod", Types.LONGVARCHAR) // Do Nothing
                .build();

        Object[] parameters = pMapper.getParameter();
        int[] parameterTypes = pMapper.getParameterTypes();

        assertThat(parameters[0]).isEqualTo("");
        assertThat(parameterTypes[0]).isEqualTo(Types.VARCHAR);

        assertThat(parameters[1]).isEqualTo("Test");
        assertThat(parameterTypes[1]).isEqualTo(Types.VARCHAR);

        assertThat(parameters[2]).isEqualTo("1000");
        assertThat(parameterTypes[2]).isEqualTo(Types.VARCHAR);

        assertThat(parameters[3]).isEqualTo("NormalNoMod");
        assertThat(parameterTypes[3]).isEqualTo(Types.LONGVARCHAR);
    }


}