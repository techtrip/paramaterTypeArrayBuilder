package com.techtrip.examples.techtrip.dbparams.model;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Class that allows for building parallel object arrays for inserting into JDBCTemplate as args and arg types
 * as specified by the JdbcOperations Interface
 * <p>
 * Uses a typical Builder approach to build up the arrays in the given order
 */
public class ParameterTypeArrayBuilder {

    /**
     * ArrayLists keep insertion order
     * <p>
     * Caution WILL ALLOW NULLS!
     */
    private List<Object> parameter = new ArrayList<>();
    private List<Integer> parameterTypes = new ArrayList<>();

    public Object[] getParameter() {
        return parameter.toArray();
    }

    public int[] getParameterTypes() {
        /**
         * Convert Integer array to int array with stream
         */
        return parameterTypes.stream().mapToInt(i -> i).toArray();
    }

    private ParameterTypeArrayBuilder(Builder builder) {
        parameter.addAll(builder.parameters);
        parameterTypes.addAll(builder.parameterTypes);
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static final class Builder {
        private List<Object> parameters = new ArrayList<>();
        private List<Integer> parameterTypes = new ArrayList<>();

        private Builder() {
        }

        public Builder parameterWihType(Object paramater, Integer paramaterType) {
            parameters.add(paramater);
            parameterTypes.add(paramaterType);
            return this;
        }

        public <K, V> Builder parameterWihType(K paramater, Integer paramatertype, Function<K, V> parameterModifier) {
            parameters.add(parameterModifier.apply(paramater));
            parameterTypes.add(paramatertype);
            return this;
        }

        public ParameterTypeArrayBuilder build() {
            return new ParameterTypeArrayBuilder(this);
        }
    }

}

